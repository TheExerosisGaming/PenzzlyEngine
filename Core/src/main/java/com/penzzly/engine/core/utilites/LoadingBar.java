package com.penzzly.engine.core.utilites;


import com.penzzly.engine.architecture.functions.compat.Function;
import com.penzzly.engine.architecture.utilites.Clarifiers;
import org.jetbrains.annotations.NotNull;

import static com.penzzly.engine.architecture.utilites.Clarifiers.Integer;
import static com.penzzly.engine.architecture.utilites.Clarifiers.Text;
import static java.lang.Math.round;
import static org.apache.commons.lang.StringUtils.repeat;

public class LoadingBar implements Function<Double, String> {
	private double unitSize;
	private int length;
	private final Object loaded;
	private final Object unloaded;
	
	public LoadingBar(@NotNull @Integer Number length, @Text Object loaded, @Text Object unloaded) {
		unitSize = 1D / (this.length = length.intValue());
		this.loaded = loaded;
		this.unloaded = unloaded;
	}
	
	@NotNull
	public String getAt(@NotNull @Clarifiers.Double Number percent) {
		int loaded = (int) round(percent.doubleValue() / unitSize);
		return repeat(this.loaded.toString(), loaded) + repeat(unloaded.toString(), length - loaded);
	}
	
	@Override
	public String apply(@NotNull Double percent) {
		return getAt(percent);
	}
}