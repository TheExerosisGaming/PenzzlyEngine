package com.penzzly.packages.factions.components.grizzly.unconfigurable;

import com.penzzly.engine.architecture.base.Component;
import com.penzzly.engine.core.base.configuration.YamlConfiguration;
import com.penzzly.packages.factions.components.grizzly.unconfigurable.classes.GrizzlyClassesComponent;
import com.penzzly.packages.factions.components.temp.GrizzlyItemStats;

import static com.penzzly.engine.core.base.Configurations.ymlConfig;
import static java.util.stream.Collectors.toList;

public class GrizzlyUnconfigurableComponent extends Component {
	
	public GrizzlyUnconfigurableComponent() {
		YamlConfiguration config = addChild(ymlConfig().enable());
		
		addChild(new GrizzlyItemStats());
		addChild(new GrizzlyFoodComponent());
		addChild(new GrizzlyClassesComponent());
		
		//Display all the unconfigurable components.
		config.set("Unconfigurable", getChildren()
				.stream()
				.filter(child -> !child.equals(config))
				.map(child -> child.getClass().getSimpleName())
				.collect(toList()));
	}
}
