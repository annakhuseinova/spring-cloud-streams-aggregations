package com.annakhuseinova.springcloudstreamsaggregations.services;

import com.annakhuseinova.springcloudstreamsaggregations.bindings.EmployeeListenerBinding;
import com.annakhuseinova.springcloudstreamsaggregations.model.Employee;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@EnableBinding(EmployeeListenerBinding.class)
@RequiredArgsConstructor
public class EmployeeKTableListener {

    private final RecordBuilder recordBuilder;

    @StreamListener("employee-input-channel")
    public void process(KStream<String, Employee> input){
        input.map((key, value)-> KeyValue.pair(value.getId(), value))
                .peek((key, value)-> log.info("Key = " + key + " Value = " + value))
                .toTable()
                .groupBy((key, value)-> KeyValue.pair(value.getDepartment(), value))
                .aggregate(
                        // setting the initial state
                        ()-> recordBuilder.init(),
                        // defining adder. Upserted record will be passed to this adder
                        (key, value, aggValue)-> recordBuilder.aggregate(value, aggValue),
                        // defining subtractor. The record deleted during upsert will be passed to this subtractor
                        (key, value, aggValue)-> recordBuilder.subtract(value, aggValue)
                ).toStream().foreach((key, value)-> log.info("Key = " + key + " Value = " + value));
    }
}
