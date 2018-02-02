package com.penzzly.engine.core.base.configuration.serialization.alpha;

import com.penzzly.engine.architecture.base.Component;
import org.jetbrains.annotations.NotNull;

public class TempFormat extends Component {
	private Object caller;
	
	@NotNull
	public TempFormat unsafely(Object caller) {
		this.caller = caller;
		return this;
	}
	
	
}
