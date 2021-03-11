package org.diploma.app.validation;

import javax.validation.GroupSequence;

@GroupSequence({FirstOrder.class, SecondOrder.class})
public interface ValidationOrder {}
