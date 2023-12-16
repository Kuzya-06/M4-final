package ru.kuznetsov;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisStringCommands;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.kuznetsov.dao.CityDAO;
import ru.kuznetsov.dao.CountryDAO;
import ru.kuznetsov.domain.City;
import ru.kuznetsov.domain.Country;
import ru.kuznetsov.domain.CountryLanguage;
import ru.kuznetsov.redis.CityCountry;
import ru.kuznetsov.redis.Language;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private final SessionFactory sessionFactory;
    private final RedisClient redisClient;

    private final ObjectMapper mapper;

    private final CityDAO cityDAO;
    private final CountryDAO countryDAO;

    public Main() {
        sessionFactory = prepareRelationalDb();
        cityDAO = new CityDAO(sessionFactory);
        countryDAO = new CountryDAO(sessionFactory);

        redisClient = prepareRedisClient();
        mapper = new ObjectMapper();
    }

    private SessionFactory prepareRelationalDb() {
        logger.info("Create SessionFactory");
        final SessionFactory sessionFactory;
        Properties properties = new Properties();
        properties.put(Environment.DIALECT, "org.hibernate.dialect.MySQL8Dialect");
        properties.put(Environment.DRIVER, "com.p6spy.engine.spy.P6SpyDriver");
        properties.put(Environment.URL, "jdbc:p6spy:mysql://localhost:3306/world");
        properties.put(Environment.USER, "root");
        properties.put(Environment.PASS, "root");
        properties.put(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread");
        properties.put(Environment.HBM2DDL_AUTO, "validate");
        properties.put(Environment.STATEMENT_BATCH_SIZE, "100");

        sessionFactory = new Configuration()
                .addAnnotatedClass(City.class)
                .addAnnotatedClass(Country.class)
                .addAnnotatedClass(CountryLanguage.class)
                .addProperties(properties)
                .buildSessionFactory();
        return sessionFactory;
    }

    private RedisClient prepareRedisClient() {
        logger.info("preparedRedisClient");
        RedisClient redisClient = RedisClient.create(RedisURI.create("localhost", 6379));
        try (StatefulRedisConnection<String, String> connection = redisClient.connect()) {
            System.out.println("\nConnected to Redis\n");
        }
        return redisClient;
    }

    private void shutdown() {
        logger.info("shutdown");
        if (nonNull(sessionFactory)) {
            sessionFactory.close();
        }
        if (nonNull(redisClient)) {
            redisClient.shutdown();
        }
    }

    public List<City> fetchData(Main main) {
        logger.info("fetchData begin");
        try (Session session = main.sessionFactory.getCurrentSession()) {
            List<City> allCities = new ArrayList<>();
            session.beginTransaction();
            List<Country> countries = main.countryDAO.getAll();
            int totalCount = main.cityDAO.getTotalCount();
            int step = 500;
            for (int i = 0; i < totalCount; i += step) {
                allCities.addAll(main.cityDAO.getItems(i, step));
            }
            session.getTransaction().commit();
            logger.info("fetchData end");
            return allCities;
        }
    }

    public static void main(String[] args) {
        Main main = new Main();
        List<City> allCities = main.fetchData(main);

        List<CityCountry> preparedData = main.transformData(allCities);
        main.pushToRedis(preparedData);

        //закроем текущую сессию, чтоб точно делать запрос к БД, а не вытянуть данные из кэша
        main.sessionFactory.getCurrentSession().close();

        //выбираем случайных 10 id городов
        //так как мы не делали обработку невалидных ситуаций, используй существующие в БД id
        List<Integer> ids = List.of(999, 1, 4021, 4, 4000, 89, 3458, 10, 4020, 102); // // Redis:	64 ms <- MySQL:	68 ms
//        List<Integer> ids = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10); // Redis:	65 ms -> MySQL:	55 ms

        long startRedis = System.currentTimeMillis();
        main.testRedisData(ids);
        long stopRedis = System.currentTimeMillis();

        long startMysql = System.currentTimeMillis();
        main.testMysqlData(ids);
        long stopMysql = System.currentTimeMillis();

        String redis = String.format("%s:\t%d ms\n", "Redis", (stopRedis - startRedis));
        String mySQL = String.format("%s:\t%d ms\n", "MySQL", (stopMysql - startMysql));
        logger.info(redis);
        logger.info(mySQL);
// 593321
        main.updateCityByPopulation(66, 999999);

        main.shutdown();
    }


    public void pushToRedis(List<CityCountry> data) {
        try (StatefulRedisConnection<String, String> connection = redisClient.connect()) {
            RedisStringCommands<String, String> sync = connection.sync();
            for (CityCountry cityCountry : data) {
                try {
                    sync.set(String.valueOf(cityCountry.getId()), mapper.writeValueAsString(cityCountry));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    private void testRedisData(List<Integer> ids) {
        try (StatefulRedisConnection<String, String> connection = redisClient.connect()) {
            RedisStringCommands<String, String> sync = connection.sync();
            for (Integer id : ids) {
                String value = sync.get(String.valueOf(id));
                try {
                    mapper.readValue(value, CityCountry.class);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void testMysqlData(List<Integer> ids) {
        try (Session session = sessionFactory.getCurrentSession()) {
            session.beginTransaction();
            for (Integer id : ids) {
                City city = cityDAO.getById(id);
                Set<CountryLanguage> languages = city.getCountry().getLanguages();
            }
            session.getTransaction().commit();
        }
    }

    public List<CityCountry> transformData(List<City> cities) {
        logger.info("transformData begin");
        return cities.stream().map(city -> {
            CityCountry res = new CityCountry();
            res.setId(city.getId());
            res.setName(city.getName());
            res.setPopulation(city.getPopulation());
            res.setDistrict(city.getDistrict());

            Country country = city.getCountry();
            res.setAlternativeCountryCode(country.getCode2());
            res.setContinent(country.getContinent());
            res.setCountryCode(country.getCode());
            res.setCountryName(country.getName());
            res.setCountryPopulation(country.getPopulation());
            res.setCountryRegion(country.getRegion());
            res.setCountrySurfaceArea(country.getSurfaceArea());
            Set<CountryLanguage> countryLanguages = country.getLanguages();
            Set<Language> languages = countryLanguages.stream().map(cl -> {
                Language language = new Language();
                language.setLanguage(cl.getLanguage());
                language.setOfficial(cl.getOfficial());
                language.setPercentage(cl.getPercentage());
                return language;
            }).collect(Collectors.toSet());
            res.setLanguages(languages);

            return res;
        }).collect(Collectors.toList());

    }

    // для проверки работоспособности Dockerfile
    public void updateCityByPopulation(Integer id, Integer populationUpdate) {
        City cityUp = cityDAO.updateCity(id, populationUpdate);
        logger.info("Name City Update: "+cityUp.getName());
        CityCountry cityCountry;
        try (StatefulRedisConnection<String, String> connection = redisClient.connect()) {
            RedisStringCommands<String, String> sync = connection.sync();

            String value = sync.get(String.valueOf(id));
            try {
                cityCountry = mapper.readValue(value, CityCountry.class);
                cityCountry.setCountryPopulation(populationUpdate);
                sync.set(value, mapper.writeValueAsString(cityCountry));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
    }

}
