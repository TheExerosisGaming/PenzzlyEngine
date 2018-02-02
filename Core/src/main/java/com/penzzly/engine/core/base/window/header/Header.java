package com.penzzly.engine.core.base.window.header;

import com.penzzly.engine.core.base.window.elements.Text;
import com.penzzly.engine.core.utilites.time.Duration;
import org.jetbrains.annotations.NotNull;

import static com.penzzly.engine.architecture.utilites.Clarifiers.Millis;

public interface Header extends Text<Header> {
	
	//--In Out Stay--
	@NotNull
	default Header fade(@NotNull Duration in, @NotNull Duration stay, @NotNull Duration out) {
		return fade(in.toMillis(), stay.toMillis(), out.toMillis());
	}
	
	@NotNull Header fade(@Millis Number in, @Millis Number stay, @Millis Number out);
	
	
	//--In Out--
	@NotNull
	default Header fade(@NotNull Duration in, @NotNull Duration out) {
		return fade(in.toMillis(), out.toMillis());
	}
	
	@NotNull Header fade(@Millis Number in, @Millis Number out);
	
	
	//--In--
	@NotNull
	default Header fadeIn(@NotNull Duration duration) {
		return fadeIn(duration.toMillis());
	}
	
	@NotNull Header fadeIn(@Millis Number duration);
	
	
	//--Out--
	@NotNull
	default Header fadeOut(@NotNull Duration duration) {
		return fadeOut(duration.toMillis());
	}
	
	@NotNull Header fadeOut(@Millis Number duration);
	
	
	//--Stay--
	@NotNull
	default Header stay(Duration duration) {
		return stay(duration);
	}
	
	@NotNull Header stay(@Millis Number duration);
}