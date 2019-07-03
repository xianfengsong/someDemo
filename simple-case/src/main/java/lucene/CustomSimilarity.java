package lucene;

import org.apache.lucene.search.similarities.DefaultSimilarity;

/**
 * Created by root on 18-3-22.
 */
public class CustomSimilarity extends DefaultSimilarity {

    @Override
    public float idf(long docFreq, long numDocs) {
        return 0.08f;
    }

    @Override
    public float tf(float freq) {
        if (freq > 1.0f) {
            freq = 1.0f;
        }
        return super.tf(freq);
    }
}
