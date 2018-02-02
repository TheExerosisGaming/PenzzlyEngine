package com.penzzly.packages.factions.components.grizzly.unconfigurable.classes;

import com.penzzly.engine.architecture.base.Component;

public class GrizzlyClassesComponent extends Component {
	public GrizzlyClassesComponent() {
		addChild(new GrizzlyArcher());
		addChild(new GrizzlyMiner());
		addChild(new GrizzlyBard());
	}
}
