package com.penzzly.engine.core.base.window.footer;

import com.penzzly.engine.core.base.window.TransactionHandler;
import com.penzzly.engine.core.utilites.time.Duration;
import io.reactivex.disposables.Disposable;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.penzzly.engine.core.utilites.bukkit.PacketUtil.sendActionBar;
import static com.penzzly.engine.core.utilites.bukkit.PacketUtil.sendTooltip;
import static com.penzzly.engine.core.utilites.time.Duration.For;
import static io.reactivex.Observable.interval;
import static java.util.concurrent.TimeUnit.SECONDS;

public class FooterHandler extends TransactionHandler<Footer> {
	private final static Duration INTERVAL = For(2, SECONDS);
	
	public FooterHandler(Player player) {
		super(player);
	}
	
	@NotNull
	@Override
	protected RxFooter create() {
		return new RxFooter() {
			private final List<Disposable> disposables = new ArrayList<>();
			
			@Override
			protected void reveal() {
				if (!isRevealed()) {
					disposables.add(interval(0, INTERVAL.time(), INTERVAL.unit())
							.filter(ignored -> isShown())
							.flatMap(ignored -> textSubject)
							.subscribe(text -> sendActionBar(player, text)));
					
					disposables.add(interval(0, INTERVAL.time(), INTERVAL.unit())
							.filter(ignored -> isShown())
							.flatMap(ignored -> subtextSubject)
							.subscribe(text -> sendTooltip(player, text)));
				}
				super.reveal();
			}
			
			@Override
			protected void conceal() {
				if (isRevealed()) {
					disposables.forEach(Disposable::dispose);
					disposables.clear();
				}
				super.conceal();
			}
			
			@NotNull
			@Override
			public Footer show() {
				if (!isShown()) {
					sendActionBar(player, textSubject.getValue());
					sendTooltip(player, subtextSubject.getValue());
				}
				super.show();
				return this;
			}
			
			@NotNull
			@Override
			public Footer hide(boolean fade) {
				if (isShown() && !fade) {
					sendActionBar(player, " ");
					sendTooltip(player, " ");
				}
				super.hide();
				return this;
			}
		};
	}
}
