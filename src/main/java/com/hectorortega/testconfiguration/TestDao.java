package com.hectorortega.testconfiguration;

import java.util.Optional;

public interface TestDao {

    Optional<TestModel> get(TestModel query);
}
