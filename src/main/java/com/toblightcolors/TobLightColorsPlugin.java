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
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.util.Text;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

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
	private TobLightColorsConfig config;

	//TODO can also remove these, just for testing
	private static final int YOUR_TOB_CHEST_PURPLE_MODEL = 35448;
	private static final int YOUR_TOB_CHEST_NORMAL_MODEL = 35446;
	private static final int OTHER_TOB_CHEST_PURPLE_MODEL = 35425;
	private static final int OTHER_TOB_CHEST_NORMAL_MODEL = 35439;

	private static final int YOUR_TOB_CHEST_PURPLE_OBJ = 32993;
	private static final int YOUR_TOB_CHEST_NORMAL_OBJ = 32992;
	private static final int OTHER_TOB_CHEST_PURPLE_OBJ = 32991;
	private static final int OTHER_TOB_CHEST_NORMAL_OBJ = 32990;

	private static final List<Integer> REWARD_CHEST_IDS = Arrays.asList(33086, 33087, 33088, 33089, 33090);

	private static final int PURPLE_HSL_COLOR_LOW = 55000;
	private static final int PURPLE_HSL_COLOR_HIGH = 55500;

	private static final int LIGHT_BEAM_MODEL = 5809;

	@Override
	protected void startUp() throws Exception
	{
	}

	@Override
	protected void shutDown() throws Exception
	{
	}

	@Subscribe
	public void onChatMessage(ChatMessage event)
	{
		String message = Text.sanitize(Text.removeTags(event.getMessage()));
		if (message.contains("s") && event.getName().contains(client.getLocalPlayer().getName())) {
			spawnTobChest();
			spawnLightBeam(client.getLocalPlayer().getLocalLocation(), config.uniqueColorYour());
		}
	}

	@Subscribe
	public void onGameObjectSpawned(GameObjectSpawned event)
	{
		int objId = event.getGameObject().getId();
		if (REWARD_CHEST_IDS.contains(objId)) {
			int impostorId = client.getObjectDefinition(objId).getImpostor().getId();
			Model tobChestModel = event.getGameObject().getRenderable().getModel();

			if (impostorId == YOUR_TOB_CHEST_PURPLE_OBJ) {
				if (config.enableRecolorYour()) {
					recolorTobChest(tobChestModel);
				}
			}
			else if (impostorId == OTHER_TOB_CHEST_PURPLE_OBJ) {
				if (config.enableRecolorOther()) {
					recolorTobChest(tobChestModel);
				}
			}
			else if (impostorId == YOUR_TOB_CHEST_NORMAL_OBJ) {
				//onyl add loott beam if enabled
			}
			else if (impostorId == OTHER_TOB_CHEST_NORMAL_OBJ) {
				//onyl add loott beam if enabled
			}
		}
	}

	private void spawnLightBeam(LocalPoint tobChestPoint, Color color)
	{
		RuneLiteObject lightBeam = client.createRuneLiteObject();
		ModelData lightBeamModel = client.loadModelData(LIGHT_BEAM_MODEL)
				.cloneVertices()
				.translate(0, -180, 0)
				.cloneColors();

		lightBeamModel.recolor(lightBeamModel.getFaceColors()[0],
				JagexColor.rgbToHSL(color.getRGB(), 1.0d));

		lightBeam.setModel(lightBeamModel.light());
		//TODO maybe add animation, as config?
		//lightBeam.setAnimation(client.loadAnimation(AnimationID.RAID_LIGHT_ANIMATION));
		//lightBeam.setShouldLoop(true);
		lightBeam.setLocation(tobChestPoint, client.getPlane());
		lightBeam.setActive(true);
	}

	//TODO remove this, just testing
	private void spawnTobChest()
	{
		RuneLiteObject tobChest = client.createRuneLiteObject();
		Model tobChestModel = client.loadModel(YOUR_TOB_CHEST_PURPLE_MODEL);

		recolorTobChest(tobChestModel);

		tobChest.setModel(tobChestModel);
		tobChest.setLocation(client.getLocalPlayer().getLocalLocation(), client.getPlane());
		tobChest.setActive(true);
	}

	private void recolorTobChest(Model tobChestModel)
	{
		int replacementColor = colorToRs2hsb(config.uniqueColorYour());
		int[] faceColors1 = tobChestModel.getFaceColors1();
		for (int i = 0; i < faceColors1.length; i++)
		{
			if (faceColors1[i] > PURPLE_HSL_COLOR_LOW && faceColors1[i] < PURPLE_HSL_COLOR_HIGH) {
				log.debug("c:" + faceColors1[i]);
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