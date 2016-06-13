package com.hectorortega.aws;

public class EC2ClientException extends RuntimeException {
    public EC2ClientException(String msg) {
        super(msg);
    }
}
