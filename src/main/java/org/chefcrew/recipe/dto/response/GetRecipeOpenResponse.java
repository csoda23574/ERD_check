package org.chefcrew.recipe.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

public record GetRecipeOpenResponse(
        @JsonProperty("COOKRCP01") CookRcpInfo cookRcpInfo
) {
    public record CookRcpInfo(
            String total_count,
            List<RecipeData> row,
            Result result
    ) {
    }

    public record RecipeData(
            @JsonProperty("RCP_PARTS_DTLS") String partsDetails,
            @JsonProperty("RCP_WAY2") String cookingMethod,
            @JsonProperty("RCP_SEQ") String recipeSeq,
            @JsonProperty("INFO_NA") Float infoNa,
            @JsonProperty("INFO_PRO") Float infoPro,
            @JsonProperty("INFO_FAT") Float infoFat,
            @JsonProperty("INFO_CAR") Float infoCar,
            @JsonProperty("INFO_ENG") Float infoCal,
            @JsonProperty("RCP_NM") String recipeName,
            @JsonProperty("RCP_PAT2") String recipeType,
            @JsonProperty("MANUAL01") String manual01,
            @JsonProperty("MANUAL02") String manual02,
            @JsonProperty("MANUAL03") String manual03,
            @JsonProperty("MANUAL04") String manual04,
            @JsonProperty("MANUAL05") String manual05,
            @JsonProperty("MANUAL06") String manual06,
            @JsonProperty("MANUAL07") String manual07,
            @JsonProperty("MANUAL08") String manual08,
            @JsonProperty("MANUAL09") String manual09,
            @JsonProperty("MANUAL10") String manual10,
            @JsonProperty("MANUAL11") String manual11,
            @JsonProperty("MANUAL12") String manual12,
            @JsonProperty("MANUAL13") String manual13,
            @JsonProperty("MANUAL14") String manual14,
            @JsonProperty("MANUAL15") String manual15,
            @JsonProperty("MANUAL16") String manual16,
            @JsonProperty("MANUAL17") String manual17,
            @JsonProperty("MANUAL18") String manual18,
            @JsonProperty("MANUAL19") String manual19,
            @JsonProperty("MANUAL_IMG01") String manual_Img01,
            @JsonProperty("MANUAL_IMG02") String manual_Img02,
            @JsonProperty("MANUAL_IMG03") String manual_Img03,
            @JsonProperty("MANUAL_IMG04") String manual_Img04,
            @JsonProperty("MANUAL_IMG05") String manual_Img05,
            @JsonProperty("MANUAL_IMG06") String manual_Img06,
            @JsonProperty("MANUAL_IMG07") String manual_Img07,
            @JsonProperty("MANUAL_IMG08") String manual_Img08,
            @JsonProperty("MANUAL_IMG09") String manual_Img09,
            @JsonProperty("MANUAL_IMG10") String manual_Img10,
            @JsonProperty("MANUAL_IMG11") String manual_Img11,
            @JsonProperty("MANUAL_IMG12") String manual_Img12,
            @JsonProperty("MANUAL_IMG13") String manual_Img13,
            @JsonProperty("MANUAL_IMG14") String manual_Img14,
            @JsonProperty("MANUAL_IMG15") String manual_Img15,
            @JsonProperty("MANUAL_IMG16") String manual_Img16,
            @JsonProperty("MANUAL_IMG17") String manual_Img17,
            @JsonProperty("MANUAL_IMG18") String manual_Img18,
            @JsonProperty("MANUAL_IMG19") String manual_Img19,
            @JsonProperty("HASH_TAG") String hashTag,
            @JsonProperty("RCP_NA_TIP") String recipeTip,
            @JsonProperty("ATT_FILE_NO_MK") String attFileNoMk,
            @JsonProperty("ATT_FILE_NO_MAIN") String attFileNoMain
    ) {
        public List<String> getManuals() {
            List<String> manuals = new ArrayList<>();
            if (manual01 != null && !manual01.isEmpty()) manuals.add(manual01);
            if (manual02 != null && !manual02.isEmpty()) manuals.add(manual02);
            if (manual03 != null && !manual03.isEmpty()) manuals.add(manual03);
            if (manual04 != null && !manual04.isEmpty()) manuals.add(manual04);
            if (manual05 != null && !manual05.isEmpty()) manuals.add(manual05);
            if (manual06 != null && !manual06.isEmpty()) manuals.add(manual06);
            if (manual07 != null && !manual07.isEmpty()) manuals.add(manual07);
            if (manual08 != null && !manual08.isEmpty()) manuals.add(manual08);
            if (manual09 != null && !manual09.isEmpty()) manuals.add(manual09);
            if (manual10 != null && !manual10.isEmpty()) manuals.add(manual10);
            if (manual11 != null && !manual11.isEmpty()) manuals.add(manual11);
            if (manual12 != null && !manual12.isEmpty()) manuals.add(manual12);
            if (manual13 != null && !manual13.isEmpty()) manuals.add(manual13);
            if (manual14 != null && !manual14.isEmpty()) manuals.add(manual14);
            if (manual15 != null && !manual15.isEmpty()) manuals.add(manual15);
            if (manual16 != null && !manual16.isEmpty()) manuals.add(manual16);
            if (manual17 != null && !manual17.isEmpty()) manuals.add(manual17);
            if (manual18 != null && !manual18.isEmpty()) manuals.add(manual18);
            if (manual19 != null && !manual19.isEmpty()) manuals.add(manual19);
            return manuals;
        }

        public List<String> getManualImages() {
            List<String> manualImages = new ArrayList<>();
            manualImages.add(manual_Img01);
            manualImages.add(manual_Img02);
            manualImages.add(manual_Img03);
            manualImages.add(manual_Img04);
            manualImages.add(manual_Img05);
            manualImages.add(manual_Img06);
            manualImages.add(manual_Img07);
            manualImages.add(manual_Img08);
            manualImages.add(manual_Img09);
            manualImages.add(manual_Img10);
            manualImages.add(manual_Img11);
            manualImages.add(manual_Img12);
            manualImages.add(manual_Img13);
            manualImages.add(manual_Img14);
            manualImages.add(manual_Img15);
            manualImages.add(manual_Img16);
            manualImages.add(manual_Img17);
            manualImages.add(manual_Img18);
            manualImages.add(manual_Img19);
            return manualImages;
        }

        // 썸네일 우선순위 getter (필요시 활용)
        public String getThumbnail() {
            return attFileNoMk != null && !attFileNoMk.isEmpty() ? attFileNoMk : attFileNoMain;
        }

    }

    public record Result(
            String MSG,
            String CODE
    ) {
    }
}
