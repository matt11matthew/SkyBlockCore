package net.cloudescape.skyblock.utils;

import com.cloudescape.CloudCore;
import com.cloudescape.modules.modules.backend.BackendModule;
import com.cloudescape.utilities.HTTPRequest;
import net.cloudescape.backend.commons.rank.GlobalRank;
import org.json.JSONObject;

import java.util.UUID;

public class RankUtil {

    public RankUtil() {

    }

    public static GlobalRank getRank(UUID uuid) {
        JSONObject contents = HTTPRequest.getContentJSONObject("http://node3.cloudescape.net:5678/users/" + uuid.toString());

        if (contents != null && contents.has("currentRank")) {
            String currentRank = contents.getString("currentRank");
            return CloudCore.getModuleManager().getModule(BackendModule.class).getGlobalRankManager().getGlobalRank(currentRank);
        }
        return null;
    }

}
