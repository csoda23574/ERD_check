package org.chefcrew.recipe.dto.request;

import org.chefcrew.recipe.enums.ValueOption;

import java.util.List;

public record GetRecipeRequest(
        String foodName,
        List<String> ingredients, // 추가: 여러 재료 검색 지원
        ValueOption calorie,        //칼로리
        ValueOption fat,            //지방
        ValueOption natrium,          //나트륨
        ValueOption protien,            //단백질
        ValueOption carbohydrate,        //탄수화물
        boolean exactMatch,              // 완벽 일치 여부
        Integer startIndex,              // 검색 시작 인덱스(프론트에서 전달)
        Integer endIndex                 // 검색 종료 인덱스(프론트에서 전달)
) {
}
