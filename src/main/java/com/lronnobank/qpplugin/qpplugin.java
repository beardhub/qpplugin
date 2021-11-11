package com.lronnobank.qpplugin;

import javax.inject.Inject;

import com.google.inject.Provides;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.vars.AccountType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.util.Text;

@Slf4j
@PluginDescriptor(name = "qpplugin")
public class qpPlugin extends Plugin {
    @Inject
    private Client client;
    @Inject
    private qpConfig config;
    @Inject
    private ChatMessageManager chatMessageManager;

    @Override
    protected void startUp() throws Exception {
        /// log.info("Example started!");

    }

    @Override
    protected void shutDown() throws Exception {
        // log.info("Example stopped!");
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged gameStateChanged) {
        // if (gameStateChanged.getGameState() == GameState.LOGGED_IN) {
        //     client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "qpPlugin says " + config.greeting(), null);
        // }
    }

    @Provides
    qpConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(qpConfig.class);
    }

    private String[] chars = new String[] { "!.:|iIl ", // 3
            "(),'`", // 4
            "{}\";[]rtT", // 5
            "/\\-LcEfFjk1", // 6
            "+abBCdDeghHjKnopPqRsSuvVwxXyYzZ3457", // 7
            "^=$AGNOQU26890", // 8
            "_*mMW", // 9
            "", // 10
            "&%~", // 11
            "", // 12
            "#@"// 13
    };

    // private String teststr = "";

    private String qpifyit(String qps) {
        String[] splits = qps.split("q p");
        String result = convertqp2W(splits[0], 0) + "W";
        for (int i = 1; i < splits.length - 1; i++) {
            result += convertqp2W(splits[i], 4) + "W";
        }
        return result;
    }

    private String convertqp2W(String qps, int offset) {
        // should have no full q p
        String ws = "";
        int pixelcount = 4 + offset;// offset for W at beginning, offset is if its afterprevious
        for (int i = 0; i < qps.length(); i++) {
            pixelcount += charToPixel(qps.charAt(i));
            while (pixelcount > 9) {
                ws += "_";
                pixelcount -= 9;
            }
        }
        return ws;
    }

    private int stringToPixel(String s) {
        int res = 0;
        for (int i = 0; i < s.length(); i++) {
            res += charToPixel(s.charAt(i));
        }
        return res;
    }

    private int charToPixel(char c) {
        for (int i = 0; i < chars.length; i++) {
            if (chars[i].indexOf(c) != -1) {
                return i + 3;
            }
        }
        return 0;
    }

    private String pixelsToString(int pixels) {
        String result = "";
        int unds = 0;
        while (pixels >= 12) {
            unds++;

            result += (unds % 5 == 0 ? "_ " : "_");
            pixels -= 9;
        }
        // result+=unds+"_";
        if (pixels != 0) {
            if (pixels == 10 || pixels == 12) {
                pixels -= 4;
                result += "(";
            }
            result += chars[pixels - 3].charAt(0);
        }
        return result;
    }

    @Subscribe
    public void onChatMessage(ChatMessage event) {
        int pixels = 0;
        // final ClanRank rank = clanManager.getRank(event.getName());
        // boolean cc = event.getType() == ChatMessageType.CLANCHAT;
        // if (cc && rank != null && rank != ClanMemberRank.UNRANKED) {
        // pixels += 14;
        // }
        String msg = event.getMessage();
        // Deb/ug(msg);
        System.out.println(msg);
        if (msg.indexOf("q p") == -1)
            return;
            // event.getMessageNode().getn
        String player = event.getMessageNode().getName();
        String sender = event.getSender();

        System.out.println("p:" + player + " s:" + sender);

        if (player.indexOf(">") != -1) {
            pixels += 13;
            player = player.substring(player.indexOf(">") + 1);
        }
        
        pixels += stringToPixel(Text.toJagexName(player));
        if(client.getAccountType().isIronman() || client.getAccountType().isGroupIronman()){
            pixels -= 13;//for icon
        }
        //my name
        String nam = client.getLocalPlayer().getName();
        System.out.println(nam);
        pixels -= stringToPixel(nam);
        // if (cc && clanManager.getRank("lronNoBank") != ClanMemberRank.UNRANKED) {
        // pixels -= 14;
        // }
        String m = "";// + (cc ? "/" : "");
        while (msg.indexOf("q p") != -1) {

            pixels += stringToPixel(msg.substring(0, msg.indexOf("q p")));
            pixels += 4;// line up w with q
            m += pixelsToString(pixels) + "W";
            pixels = 4;
            msg = msg.substring(msg.indexOf("q p") + 3);
        }

        System.out.println(m);
        // client.addChatMessage(ChatMessageType.TRADE, "", m, null);
        final String message = new ChatMessageBuilder().append(m).build();
        chatMessageManager
                // .queue(QueuedMessage.builder().type(ChatMessageType.TRADE).runeLiteFormattedMessage(m).build());
                .queue(QueuedMessage.builder().type(ChatMessageType.TRADE).runeLiteFormattedMessage(message).build());
    }
}
