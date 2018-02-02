package com.penzzly.engine.core.base.window.footer;

import com.penzzly.engine.core.base.window.elements.Text;
import com.penzzly.engine.core.utilites.time.Duration;
import org.jetbrains.annotations.NotNull;

import static com.penzzly.engine.architecture.utilites.Clarifiers.Millis;
import static com.penzzly.engine.core.base.Scheduler.in;

public interface Footer extends Text<Footer> {
	//--Show--
	@NotNull
	default Footer show(@NotNull Duration duration) {
		return show(duration.toMillis());
	}
	
	@NotNull
	default Footer show(@NotNull @Millis Number duration) {
		addChild(in(duration).milliseconds().run((Runnable) this::hide));
		return show();
	}
	
	//--Hide--
	@NotNull
	@Override
	default Footer hide() {
		return hide(false);
	}
	
	@NotNull Footer hide(boolean fade);
}
