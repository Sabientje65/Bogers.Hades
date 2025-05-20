package org.primagen.hades;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.clan.ClanChannel;
import net.runelite.api.clan.ClanChannelMember;
import net.runelite.api.events.ClanChannelChanged;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@PluginDescriptor(
	name = "Example"
)
public class HadesPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private HadesConfig config;

	@Override
	protected void startUp() throws Exception
	{
		// TODO: RemoteConfigurationManager -> downloadRemoteConfiguration
		log.info("Example started!");
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("Example stopped!");
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() == GameState.LOGGED_IN)
		{


//			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Example says " + config.greeting(), null);
		}
	}

	// https://github.com/runelite/plugin-hub


	/*
		naming conventions are enforced based on event name:

		final String preferredName = "on" + parameterClazz.getSimpleName();
		Preconditions.checkArgument(method.getName().equals(preferredName), "Subscribed method " + method + " should be named " + preferredName);
	 */

	@Subscribe
	public void onClanChannelChanged(ClanChannelChanged clanChannelChanged) {
		ClanChannel clanChannel = clanChannelChanged.getClanChannel();
		if (clanChannel == null) return;

		// should make which clans to notify for configurable
		if (!clanChannel.getName().equalsIgnoreCase("osg")) return;

		List<ClanChannelMember> knownReporters = clanChannel.getMembers()
				.stream()
				.filter(member -> HadesKnownReporterService.isKnownReporter(member.getName()))
				.collect(Collectors.toList());

		for (ClanChannelMember reporter : knownReporters) {
			warnKnownReporterPresentInClan(reporter);
		}



//		ClanChannelMember member = clanChannel.findMember("Primagen");
//		if (member == null) return;
//		warnKnownReporterPresentInClan(member);
	}

//	@Subscribe
//	public void onClanMemberJoined(ClanMemberJoined clanMemberJoined) {
////		clanMemberJoined.getClanMember().getName()
//	}

	private void warnKnownReporterPresentInClan(ClanChannelMember clanMember) {

		// for colored messages:
		// https://github.com/runelite/runelite/blob/master/runelite-client/src/main/java/net/runelite/client/plugins/chatcommands/ChatCommandsPlugin.java#L698
		// https://github.com/runelite/runelite/blob/master/runelite-client/src/main/java/net/runelite/client/plugins/chatnotifications/ChatNotificationsPlugin.java#L240
		String message = new StringBuilder()
				.append("Known reporter ")
				.append("<col" + ChatColorType.HIGHLIGHT.name() + "><u>")
				.append(clanMember.getName())
				.append("</u><col" + ChatColorType.NORMAL.name() + ">")
				.append(" is present in clan chat!")
				.toString();

		client.addChatMessage(ChatMessageType.GAMEMESSAGE, "HadesPlugin", message, null);
	}

	// https://static.runelite.net/runelite-api/apidocs/net/runelite/api/events/ClanMemberJoined.html
	// https://static.runelite.net/runelite-api/apidocs/net/runelite/api/events/ClanChannelChanged.html

	@Provides
	HadesConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(HadesConfig.class);
	}
}
