package com.penzzly.engine.core.base.configuration;

import com.penzzly.engine.architecture.functions.Optional;
import com.penzzly.engine.architecture.functions.compat.Consumer;

public interface Configuration<Type> extends Optional<Type>, Consumer<Type> {
	
	@Override
	default void accept(Type value) {
		set(value);
	}
	
	void set(Type value);
	
	void save();
	
	void load();
	
}