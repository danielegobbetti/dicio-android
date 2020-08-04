package org.dicio.dicio_android.components.processing;

import android.util.Log;

import org.dicio.component.IntermediateProcessor;
import org.dicio.component.standard.StandardResult;
import org.dicio.dicio_android.components.output.SearchOutput;
import org.dicio.dicio_android.util.ConnectionUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static org.dicio.dicio_android.sentences.Sentences_en.search;
import static org.dicio.dicio_android.util.ConnectionUtils.getPageJson;

public class QwantProcessor implements IntermediateProcessor<StandardResult, List<SearchOutput.Data>> {

    private static final String qwantSearchUrl = "https://api.qwant.com/api/search/web";


    @Override
    public List<SearchOutput.Data> process(StandardResult data) throws Exception {
        JSONObject json = getPageJson(qwantSearchUrl
                + "?count=10&offset=20&t=dicio&uiv=1&locale=en_gb&q="
                + ConnectionUtils.urlEncode(data.getCapturingGroup(search.what).trim()));
        Log.d("json", json.toString());
        JSONArray items = json.getJSONObject("data").getJSONObject("result").getJSONArray("items");

        List<SearchOutput.Data> result = new ArrayList<>();
        for (int i = 0; i < items.length(); ++i) {
            SearchOutput.Data searchResult = new SearchOutput.Data();
            JSONObject item = items.getJSONObject(i);

            searchResult.title = item.getString("title");
            searchResult.thumbnailUrl = "https:" + item.getString("favicon");
            searchResult.url = item.getString("url");
            searchResult.description = item.getString("desc");
            result.add(searchResult);
        }

        return result;
    }
}