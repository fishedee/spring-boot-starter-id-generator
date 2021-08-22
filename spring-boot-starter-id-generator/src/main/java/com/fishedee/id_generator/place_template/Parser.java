package com.fishedee.id_generator.place_template;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public interface Parser {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class Result{
        private int nextIndex;
        private Long argument;
        private boolean isSuccess;
    }
    Result parse(String input,int index);
}
