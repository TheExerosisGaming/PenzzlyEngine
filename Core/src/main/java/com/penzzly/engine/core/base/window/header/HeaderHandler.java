package com.penzzly.engine.core.base.window.header;

import com.penzzly.engine.core.base.window.BaseElement;
import com.penzzly.engine.core.base.window.TransactionHandler;
import io.reactivex.disposables.Disposable;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.penzzly.engine.core.base.Scheduler.in;
import static com.penzzly.engine.core.utilites.bukkit.PacketUtil.*;
import static java.lang.System.currentTimeMillis;

public class HeaderHandler extends TransactionHandler<Header> {
	
	public HeaderHandler(Player player) {
		super(player);
	}
	
	@NotNull
	@Override
	protected BaseElement<Header> create() {
		return new RxHeader() {
			private final List<Disposable> disposables = new ArrayList<>();
			private long startTime;
			
			@Override
			protected void init() {
				addInternalChild(in(fadeIn + stay + fadeOut)
						.milliseconds()
						.run(this::complete)
				);
			}
			
			@Override
			protected void reveal() {
				if (!isRevealed()) {
					startTime = currentTimeMillis();
					disposables.add(textSubject.subscribe(text ->
							titleText(player, text, null)));
					disposables.add(subtextSubject.subscribe(text ->
							titleText(player, text, null)));
				}
				super.reveal();
			}
			
			@Override
			protected void conceal() {
				if (isRevealed()) {
					disposables.forEach(Disposable::dispose);
					disposables.clear();
					titleReset(player, false);
				}
				super.conceal();
			}
			
			@NotNull
			@Override
			public Header show() {
				if (!isShown()) {
					long revealedFor = currentTimeMillis() - startTime;
					int in = fadeIn;
					int stay = this.stay;
					int out = fadeOut;
					
					if ((in -= revealedFor) < 0) {
						if ((stay += in) < 0) {
							if ((out += stay) < 0) {
								return this;
							}
						}
					}
					titleTimes(player, in, stay, out);
				}
				return super.show();
			}
			
			@NotNull
			@Override
			public Header hide() {
				if (isShown()) {
					titleReset(player, true);
				}
				return super.hide();
			}
		};
	}
}