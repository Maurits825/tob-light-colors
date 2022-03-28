package com.toblightcolors;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

import java.awt.Color;

@ConfigGroup("toblightcolors")
public interface TobLightColorsConfig extends Config
{
	@ConfigItem(
			keyName = "enableLightAnimation",
			name = "Enable light animation",
			description = "Enable adding an animation to the light beam",
			position = 0
	)
	default boolean enableLightAnimation()
	{
		return false;
	}

	@ConfigSection(
			name = "Your chest",
			description = "Settings when you receive a unique in your name",
			position = 0,
			closedByDefault = false
	)
	String yourChest = "yourChest";

	@ConfigItem(
			keyName = "enableRecolorYour",
			name = "Enable recolor",
			description = "Enable recoloring the chest when you receive a unique",
			position = 0,
			section = yourChest
	)
	default boolean enableRecolorYour()
	{
		return true;
	}

	@ConfigItem(
			keyName = "enableUniqueLightBeamYour",
			name = "Enable unique light beam",
			description = "Enable adding the cox light beam above the chest when you receive a unique",
			position = 1,
			section = yourChest
	)
	default boolean enableUniqueLightBeamYour()
	{
		return true;
	}

	@ConfigItem(
			keyName = "uniqueColorYour",
			name = "Unique color",
			description = "Color of the chest and light when you receive a unique",
			position = 2,
			section = yourChest
	)
	default Color uniqueColorYour()
	{
		return Color.RED;
	}

	@ConfigItem(
			keyName = "enableStandardLightBeamYour",
			name = "Enable standard light beam",
			description = "Enable adding the cox light beam above the chest when receiving standard loot",
			position = 3,
			section = yourChest
	)
	default boolean enableStandardLightBeamYour()
	{
		return true;
	}

	@ConfigItem(
			keyName = "standardColorYour",
			name = "Standard color",
			description = "Color of the light when receiving standard loot",
			position = 4,
			section = yourChest
	)
	default Color standardColorYour()
	{
		return Color.WHITE;
	}

	@ConfigSection(
			name = "Other chest",
			description = "Settings when someone else receives a unique",
			position = 1,
			closedByDefault = false
	)
	String otherChest = "otherChest";

	@ConfigItem(
			keyName = "enableRecolorOther",
			name = "Enable recolor",
			description = "Enable recoloring the chest when someone else receives a unique",
			position = 0,
			section = otherChest
	)
	default boolean enableRecolorOther()
	{
		return true;
	}

	@ConfigItem(
			keyName = "enableUniqueLightBeamOther",
			name = "Enable unique light beam",
			description = "Enable adding the cox light beam above the chest when someone else receives a unique",
			position = 1,
			section = otherChest
	)
	default boolean enableUniqueLightBeamOther()
	{
		return true;
	}

	@ConfigItem(
			keyName = "uniqueColorOther",
			name = "Unique color",
			description = "Color of the chest and light when someone else receives a unique",
			position = 2,
			section = otherChest
	)
	default Color uniqueColorOther()
	{
		return Color.MAGENTA;
	}

	@ConfigItem(
			keyName = "enableStandardLightBeamOther",
			name = "Enable standard light beam",
			description = "Enable adding the cox light beam above the chest when someone else receives standard loot",
			position = 3,
			section = otherChest
	)
	default boolean enableStandardLightBeamOther()
	{
		return false;
	}

	@ConfigItem(
			keyName = "standardColorOther",
			name = "Standard color",
			description = "Color of the light when someone else receives standard loot",
			position = 4,
			section = otherChest
	)
	default Color standardColorOther()
	{
		return Color.WHITE;
	}

}
