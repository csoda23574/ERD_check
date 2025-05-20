package org.chefcrew.common.validate;

import org.springframework.stereotype.Component;

@Component
public class Validation {
    public boolean isExistUserByUserId(Long userId) {
        // TODO: 실제 유저 존재 여부 확인 로직 구현 필요
        return true;
    }
    public boolean isExistRecipeByRecipeName(String recipeName) {
        // TODO: 실제 레시피 존재 여부 확인 로직 구현 필요
        return true;
    }
}
