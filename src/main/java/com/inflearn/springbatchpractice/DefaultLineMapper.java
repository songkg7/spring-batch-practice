package com.inflearn.springbatchpractice;

import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.LineTokenizer;

public class DefaultLineMapper<T> implements LineMapper<T> {

    private LineTokenizer lineTokenizer;
    private FieldSetMapper<T> fieldSetMapper;

    public DefaultLineMapper(LineTokenizer lineTokenizer, FieldSetMapper<T> fieldSetMapper) {
        this.lineTokenizer = lineTokenizer;
        this.fieldSetMapper = fieldSetMapper;
    }

    @Override
    public T mapLine(String line, int lineNumber) throws Exception {
        return fieldSetMapper.mapFieldSet(lineTokenizer.tokenize(line));
    }
}
