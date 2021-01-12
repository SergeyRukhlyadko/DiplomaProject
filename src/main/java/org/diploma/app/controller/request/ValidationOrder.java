package org.diploma.app.controller.request;

import javax.validation.GroupSequence;

@GroupSequence({FirstOrder.class, SecondOrder.class})
public interface ValidationOrder {}
