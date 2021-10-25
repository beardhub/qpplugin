package com.lronnobank.qpplugin;

import javax.inject.Inject;

import net.runelite.api.ChatMessageType;
import net.runelite.api.events.ChatMessage;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.util.Text;

public class qpplugin {
    // uim img 3
    @Inject
	private ChatMessageManager chatMessageManager;

    private String[] chars = new String[] {
        ".:|!iIl ",//3
        ",'()`",//4
        "\";[]{}rtT",//5 
        "/\\-LcEfFjk1",//6
        "+abBCdDeghHjKnopPqRsSuvVwxXyYzZ3457",//7
        "=^$AGNOQU26890",//8
        "_*mMW",//9
        "",//10
        "&%~",//11
        "",//12
        "#@"//13
    };

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
        while (pixels >= 12) {
            result += "_";
            pixels -= 9;
        }
        if (pixels != 0) {
            if (pixels == 10 || pixels == 12) {
                pixels -= 4;
                result += ",";
            }
            result += chars[pixels - 3].charAt(0);
        }
        return result;
    }
@Subscribe
public void onChatMessage(ChatMessage event){
        int pixels = 0;
       // final ClanMemberRank rank = clanManager.getRank(event.getName());
        //boolean cc = event.getType() == ChatMessageType.CLANCHAT;
     //   if (cc && rank != null && rank != ClanMemberRank.UNRANKED) {
       //     pixels += 14;
       // }
        String msg = event.getMessage();
        String player = event.getName();
        if (player.indexOf(">") != -1) {
            pixels += 13;
            player = player.substring(player.indexOf(">") + 1);
        }
        pixels += stringToPixel(Text.toJagexName(player));
        pixels -= 13;
        pixels -= stringToPixel("lronNoBank");
     //   if (cc && clanManager.getRank("lronNoBank") != ClanMemberRank.UNRANKED) {
      //      pixels -= 14;
      //  }
        String m = "<img=3>lronNoBank: ";// + (cc ? "/" : "");
        while (msg.indexOf("q p") != -1) {
            pixels += stringToPixel(msg.substring(0, msg.indexOf("q p")));
            pixels += 4;// line up w with q
            m += pixelsToString(pixels) + "W";
            pixels = 4;
            msg = msg.substring(msg.indexOf("q p") + 3);
        }
        final String message = new ChatMessageBuilder().append(m).build();
        chatMessageManager
                .queue(QueuedMessage.builder().type(ChatMessageType.TRADE).runeLiteFormattedMessage(message).build());
    }
}
