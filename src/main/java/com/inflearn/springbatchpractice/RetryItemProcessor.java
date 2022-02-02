package com.inflearn.springbatchpractice;

import com.inflearn.springbatchpractice.customer.Customer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.classify.BinaryExceptionClassifier;
import org.springframework.classify.Classifier;
import org.springframework.retry.RecoveryCallback;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.support.DefaultRetryState;
import org.springframework.retry.support.RetryTemplate;

@Slf4j
@RequiredArgsConstructor
public class RetryItemProcessor implements ItemProcessor<String, Customer> {
    private final RetryTemplate retryTemplate;

    private int count = 0;

    @Override
    public Customer process(String item) throws Exception {
        Classifier<Throwable, Boolean> rollbackClassifier = new BinaryExceptionClassifier(true);

        return retryTemplate.execute(new RetryCallback<Customer, RuntimeException>() {
            @Override
            public Customer doWithRetry(RetryContext context) throws RuntimeException {
                if (item.equals("1") || item.equals("2")) {
                    count++;
                    throw new RetryableException("failed count : " + count);
                }
                return Customer.of(item);
            }
        }, new RecoveryCallback<Customer>() {
            @Override
            public Customer recover(RetryContext context) throws Exception {
                return Customer.of(item);
            }
        }, new DefaultRetryState(item, rollbackClassifier)); // RetryState 가 설정되어 있지 않으면 아이템이 단순 반복처리된다.
    }
}
