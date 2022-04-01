package com.toblightcolors;

import com.google.inject.Provides;
import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.AnimationID;
import net.runelite.api.Client;
import net.runelite.api.JagexColor;
import net.runelite.api.Model;
import net.runelite.api.ModelData;
import net.runelite.api.RuneLiteObject;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import java.awt.Color;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@PluginDescriptor(
		name = "ToB Light Colors",
		description = "Recolors the ToB chest and adds a light beam above the chest"
)
public class TobLightColorsPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private TobLightColorsConfig config;

	private static final int YOUR_TOB_CHEST_PURPLE_OBJ = 32993;
	private static final int YOUR_TOB_CHEST_NORMAL_OBJ = 32992;
	private static final int OTHER_TOB_CHEST_PURPLE_OBJ = 32991;
	private static final int OTHER_TOB_CHEST_NORMAL_OBJ = 32990;

	private static final List<Integer> REWARD_CHEST_IDS = Arrays.asList(33086, 33087, 33088, 33089, 33090);

	private static final int PURPLE_HSL_COLOR_LOW = 55000;
	private static final int PURPLE_HSL_COLOR_HIGH = 55500;

	private static final int LIGHT_BEAM_MODEL = 5809;

	private Map<Integer, RuneLiteObject> lightBeamsObjects = new HashMap<>();

	@Override
	protected void startUp() throws Exception
	{
	}

	@Override
	protected void shutDown() throws Exception
	{
		clientThread.invokeLater(() ->
		{
			clearAllLightBeams();
			return true;
		});
	}

	@Subscribe
	public void onGameObjectSpawned(GameObjectSpawned event)
	{
		int objId = event.getGameObject().getId();
		if (REWARD_CHEST_IDS.contains(objId)) {
			int impostorId = client.getObjectDefinition(objId).getImpostor().getId();
			Model tobChestModel = event.getGameObject().getRenderable().getModel();
			LocalPoint tobChestPoint = event.getGameObject().getLocalLocation();

			if (impostorId == YOUR_TOB_CHEST_PURPLE_OBJ) {
				if (config.enableRecolorYour()) {
					recolorTobChest(tobChestModel);
				}
				if (config.enableUniqueLightBeamYour()) {
					spawnLightBeam(tobChestPoint, config.uniqueColorYour(), objId);
				}
			}
			else if (impostorId == OTHER_TOB_CHEST_PURPLE_OBJ) {
				if (config.enableRecolorOther()) {
					recolorTobChest(tobChestModel);
				}
				if (config.enableUniqueLightBeamOther()) {
					spawnLightBeam(tobChestPoint, config.uniqueColorOther(), objId);
				}
			}
			else if (impostorId == YOUR_TOB_CHEST_NORMAL_OBJ) {
				if (config.enableStandardLightBeamYour()) {
					spawnLightBeam(tobChestPoint, config.standardColorYour(), objId);
				}
			}
			else if (impostorId == OTHER_TOB_CHEST_NORMAL_OBJ) {
				if (config.enableStandardLightBeamOther()) {
					spawnLightBeam(tobChestPoint, config.standardColorOther(), objId);
				}
			}
		}
	}

	@Subscribe
	public void onGameObjectDespawned(GameObjectDespawned event)
	{
		int objId = event.getGameObject().getId();
		if (lightBeamsObjects.containsKey(objId)) {
			lightBeamsObjects.get(objId).setActive(false);
			lightBeamsObjects.remove(objId);
		}
	}

	private void spawnLightBeam(LocalPoint point, Color color, int objId)
	{
		RuneLiteObject lightBeam = client.createRuneLiteObject();
		ModelData lightBeamModel = client.loadModelData(LIGHT_BEAM_MODEL)
				.cloneVertices()
				.translate(0, -100, 0)
				.scale(240, 200, 240)
				.cloneColors();

		lightBeamModel.recolor(lightBeamModel.getFaceColors()[0],
				JagexColor.rgbToHSL(color.getRGB(), 1.0d));

		lightBeam.setModel(lightBeamModel.light());

		if (config.enableLightAnimation()) {
			lightBeam.setAnimation(client.loadAnimation(AnimationID.RAID_LIGHT_ANIMATION));
			lightBeam.setShouldLoop(true);
		}

		lightBeam.setLocation(point, client.getPlane());
		lightBeam.setActive(true);

		lightBeamsObjects.put(objId, lightBeam);
	}

	private void clearAllLightBeams() {
		for (Map.Entry<Integer, RuneLiteObject> entry : lightBeamsObjects.entrySet()) {
			entry.getValue().setActive(false);
		}

		lightBeamsObjects.clear();
	}

	private void recolorTobChest(Model tobChestModel)
	{
		int replacementColor = colorToRs2hsb(config.uniqueColorYour());
		int[] faceColors1 = tobChestModel.getFaceColors1();
		for (int i = 0; i < faceColors1.length; i++)
		{
			if (faceColors1[i] > PURPLE_HSL_COLOR_LOW && faceColors1[i] < PURPLE_HSL_COLOR_HIGH) {
				faceColors1[i] = replacementColor;
			}
		}
	}

	private int colorToRs2hsb(Color color)
	{
		float[] hsbVals = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);

		// "Correct" the brightness level to avoid going to white at full saturation, or having a low brightness at
		// low saturation
		hsbVals[2] -= Math.min(hsbVals[1], hsbVals[2] / 2);

		int encode_hue = (int)(hsbVals[0] * 63);
		int encode_saturation = (int)(hsbVals[1] * 7);
		int encode_brightness = (int)(hsbVals[2] * 127);
		return (encode_hue << 10) + (encode_saturation << 7) + (encode_brightness);
	}

	@Provides
	TobLightColorsConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(TobLightColorsConfig.class);
	}
}
