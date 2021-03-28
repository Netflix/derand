/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package netflixoss.derand;

import ai.djl.MalformedModelException;
import ai.djl.inference.Predictor;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelNotFoundException;
import ai.djl.repository.zoo.ModelZoo;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.translate.TranslateException;

import java.io.IOException;
import java.util.*;

public class Derand {

    private static final String MODEL_URL = "https://randomly-public-us-east-1.s3.amazonaws.com/randomly_light_cnn.onnx.zip";
    private static final String MODEL_NAME = "randomly_light_cnn.onnx";
    private static final String RND = "<rnd>";


    private static final ThreadLocal<Predictor<String, Boolean>> predictorHolder = new ThreadLocal<>();

    synchronized static Optional<ZooModel<String, Boolean>> init() throws IOException, ModelNotFoundException, MalformedModelException {
        if(model == null || !model.isPresent()){
            return Optional.ofNullable(getModel());
        } else {
            return model;
        }
    }

    private static ZooModel<String, Boolean> getModel() throws MalformedModelException, ModelNotFoundException, IOException {
        Criteria<String, Boolean> criteria = Criteria.builder()
                .setTypes(String.class, Boolean.class)
                .optTranslator(new StringToEncoderTranslator())
                .optModelUrls(MODEL_URL)
                .optModelName(MODEL_NAME)
                .optEngine("OnnxRuntime")
                .build();

            return ModelZoo.loadModel(criteria);
    }

    // TODO: add retry
    private static Optional<ZooModel<String, Boolean>> model;

    static {
        try {
            model = init();
        } catch (IOException | ModelNotFoundException | MalformedModelException e) {
            model = Optional.empty();
        }
    }


    private static boolean[] predictRandomnessPerWord(String[] words) throws TranslateException, IOException {

        if(!model.isPresent()){
            throw new IOException("model can not be null");
        }

        Predictor<String, Boolean> predictor = predictorHolder.get();

        if(predictor == null){
            predictor = model.get().newPredictor();
            predictorHolder.set(predictor);
        }

        boolean[] result = new boolean[words.length];

        int idx = 0;

        for (String word: words){
            boolean isRandom = predictor.predict(word);
            result[idx] = isRandom;
            idx++;
        }

        return result;
    }

    private static String[] tokenizeWords(String[] words) throws TranslateException, IOException {

        String[] result = new String[words.length];
        boolean[] randomMask = predictRandomnessPerWord(words);
        int idx = 0;

        for(boolean isRandom: randomMask){
            if(isRandom){
                result[idx] = RND;
            } else{
                result[idx] = words[idx];
            }
            idx++;
        }

        return result;
    }

    public static String tokenize(String text) throws TranslateException, IOException {

        if (isEmpty(text)) return "";

        StringJoiner joiner = new StringJoiner(" ");

        for(String tokenizedWord :tokenizeWords(text.split(" "))){
            joiner.add(tokenizedWord);
        }

        return joiner.toString();
    }

    public static String clean(String text) throws TranslateException, IOException {

        if (isEmpty(text)) return "";

        StringJoiner joiner = new StringJoiner(" ");

        for(String tokenizedWord :tokenizeWords(text.split(" "))){
            if(!tokenizedWord.equals(RND)){
                joiner.add(tokenizedWord);
            }
        }

        return joiner.toString();

    }

    private static boolean isEmpty(String text) {
        return text == null || text.isEmpty() || text.trim().isEmpty();
    }

}