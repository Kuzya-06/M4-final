select count(city0_.id) as col_0_0_ from city city0_;

select city0_.id as id1_0_
     , city0_.country_id as country_5_0_
     , city0_.district as district2_0_
     , city0_.name as name3_0_
     , city0_.population as populati4_0_
from city city0_ limit 500;

select city0_.id as id1_0_, city0_.country_id as country_5_0_, city0_.district as district2_0_, city0_.name as name3_0_, city0_.population as populati4_0_ from city city0_ limit 500, 500;

select country0_.id as id1_1_0_
     , country0_.capital as capital16_1_0_
     , country0_.code as code2_1_0_
     , country0_.code_2 as code_3_1_0_
     , country0_.continent as continen4_1_0_
     , country0_.gnp as gnp5_1_0_
     , country0_.gnpo_id as gnpo_id6_1_0_
     , country0_.government_form as governme7_1_0_
     , country0_.head_of_state as head_of_8_1_0_
     , country0_.indep_year as indep_ye9_1_0_
     , country0_.life_expectancy as life_ex10_1_0_
     , country0_.local_name as local_n11_1_0_
     , country0_.name as name12_1_0_
     , country0_.population as populat13_1_0_
     , country0_.region as region14_1_0_
     , country0_.surface_area as surface15_1_0_
     , city1_.id as id1_0_1_
     , city1_.country_id as country_5_0_1_
     , city1_.district as district2_0_1_
     , city1_.name as name3_0_1_
     , city1_.population as populati4_0_1_
     , country2_.id as id1_1_2_
     , country2_.capital as capital16_1_2_
     , country2_.code as code2_1_2_
     , country2_.code_2 as code_3_1_2_
     , country2_.continent as continen4_1_2_
     , country2_.gnp as gnp5_1_2_
     , country2_.gnpo_id as gnpo_id6_1_2_
     , country2_.government_form as governme7_1_2_
     , country2_.head_of_state as head_of_8_1_2_
     , country2_.indep_year as indep_ye9_1_2_
     , country2_.life_expectancy as life_ex10_1_2_
     , country2_.local_name as local_n11_1_2_
     , country2_.name as name12_1_2_
     , country2_.population as populat13_1_2_
     , country2_.region as region14_1_2_
     , country2_.surface_area as surface15_1_2_
     , language3_.country_id as country_5_2_3_
     , language3_.id as id1_2_3_
     , language3_.id as id1_2_4_
     , language3_.country_id as country_5_2_4_
     , language3_.is_official as is_offic2_2_4_
     , language3_.language as language3_2_4_
     , language3_.percentage as percenta4_2_4_
from country country0_
         left outer join city city1_ on country0_.capital=city1_.id
         left outer join country country2_ on city1_.country_id=country2_.id
         left outer join country_language language3_ on country0_.id=language3_.country_id
where country0_.id=7;

select country0_.id as id1_1_, country0_.capital as capital16_1_, country0_.code as code2_1_, country0_.code_2 as code_3_1_, country0_.continent as continen4_1_, country0_.gnp as gnp5_1_, country0_.gnpo_id as gnpo_id6_1_, country0_.government_form as governme7_1_, country0_.head_of_state as head_of_8_1_, country0_.indep_year as indep_ye9_1_, country0_.life_expectancy as life_ex10_1_, country0_.local_name as local_n11_1_, country0_.name as name12_1_, country0_.population as populat13_1_, country0_.region as region14_1_, country0_.surface_area as surface15_1_ from country country0_;

select city0_.id as id1_0_0_, city0_.country_id as country_5_0_0_, city0_.district as district2_0_0_, city0_.name as name3_0_0_, city0_.population as populati4_0_0_, country1_.id as id1_1_1_, country1_.capital as capital16_1_1_, country1_.code as code2_1_1_, country1_.code_2 as code_3_1_1_, country1_.continent as continen4_1_1_, country1_.gnp as gnp5_1_1_, country1_.gnpo_id as gnpo_id6_1_1_, country1_.government_form as governme7_1_1_, country1_.head_of_state as head_of_8_1_1_, country1_.indep_year as indep_ye9_1_1_, country1_.life_expectancy as life_ex10_1_1_, country1_.local_name as local_n11_1_1_, country1_.name as name12_1_1_, country1_.population as populat13_1_1_, country1_.region as region14_1_1_, country1_.surface_area as surface15_1_1_, city2_.id as id1_0_2_, city2_.country_id as country_5_0_2_, city2_.district as district2_0_2_, city2_.name as name3_0_2_, city2_.population as populati4_0_2_, language3_.country_id as country_5_2_3_, language3_.id as id1_2_3_, language3_.id as id1_2_4_, language3_.country_id as country_5_2_4_, language3_.is_official as is_offic2_2_4_, language3_.language as language3_2_4_, language3_.percentage as percenta4_2_4_ from city city0_ left outer join country country1_ on city0_.country_id=country1_.id left outer join city city2_ on country1_.capital=city2_.id left outer join country_language language3_ on country1_.id=language3_.country_id where city0_.id=129;

