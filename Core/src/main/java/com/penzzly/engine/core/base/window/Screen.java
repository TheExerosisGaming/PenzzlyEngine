package com.penzzly.engine.core.base.window;

import com.penzzly.engine.architecture.base.Component;
import com.penzzly.engine.core.base.window.footer.Footer;
import com.penzzly.engine.core.base.window.footer.FooterHandler;
import com.penzzly.engine.core.base.window.header.Header;
import com.penzzly.engine.core.base.window.header.HeaderHandler;
import com.penzzly.engine.core.base.window.page.Page;
import com.penzzly.engine.core.base.window.page.PageHandler;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class Screen extends Component {
	@NotNull
	private final HeaderHandler headerHandler;
	@NotNull
	private final FooterHandler footerHandler;
	@NotNull
	private final PageHandler pageHandler;
	
	public Screen(Player player) {
		headerHandler = new HeaderHandler(player);
		footerHandler = new FooterHandler(player);
		pageHandler = new PageHandler(player);
	}
	
	@NotNull
	public PageHandler page() {
		return pageHandler;
	}
	
	@NotNull
	public FooterHandler footer() {
		return footerHandler;
	}
	
	@NotNull
	public HeaderHandler header() {
		return headerHandler;
	}
	
	@NotNull
	public Transaction header(@NotNull Consumer<Header> description) {
		return headerHandler.transaction(description);
	}
	
	@NotNull
	public Transaction footer(@NotNull Consumer<Footer> description) {
		return footerHandler.transaction(description);
	}
	
	@NotNull
	public Transaction page(@NotNull Consumer<Page> description) {
		return pageHandler.transaction(description);
	}
	
	@NotNull
	@Override
	public Screen enable() {
		super.enable();
		return this;
	}
	
	@NotNull
	@Override
	public Screen disable() {
		super.disable();
		return this;
	}
}
