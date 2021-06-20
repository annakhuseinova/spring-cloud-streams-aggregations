package com.annakhuseinova.springcloudstreamsaggregations.services;

import com.annakhuseinova.springcloudstreamsaggregations.bindings.EmployeeListenerBinding;
import com.annakhuseinova.springcloudstreamsaggregations.model.Employee;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@EnableBinding(EmployeeListenerBinding.class)
@RequiredArgsConstructor
public class EmployeeKStreamListener {

    private final RecordBuilder recordBuilder;

    @StreamListener("employee-input-channel")
    public void process(KStream<String, Employee> input){
        input.peek((key, value)-> log.info("Key: {}, Value: {}", key, value))
                .groupBy((key, value)-> value.getDepartment())
                // initializer lambda. Just returns the initial state of the aggregated record
                .aggregate(()-> recordBuilder.init(),
                        // aggregatorValue takes in 3 values: key, value and initial value
                        (key, value, aggValue)-> recordBuilder.aggregate(value, aggValue))
        .toStream().foreach((key, value)-> log.info("Key = " + key + " Value = " + value));
    }
}
