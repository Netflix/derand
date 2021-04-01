/**
 *
 *  Copyright 2018 Netflix, Inc.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */

package netflixoss.derand;

import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import ai.djl.translate.Batchifier;
import ai.djl.translate.Translator;
import ai.djl.translate.TranslatorContext;

import java.util.Arrays;
import java.util.List;


public final class StringToEncoderTranslator implements Translator<String, Boolean> {

    StringToEncoderTranslator() {}

    private static final List<Character> modelChars = Arrays.asList('!', '"', '#', '$', '%', '&', '\'', '(', ')', '*', '+', ',', '-', '.',
            '/', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ':', ';', '<',
            '=', '>', '?', '@', '_', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i',
            'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
            'x', 'y', 'z');

    int maxLen = 16;

    @Override
    public NDList processInput(TranslatorContext ctx, String input) {

        /**
         * This method allows to convert word "hello" to a {@modelChars.lenght()} dimensional array with values representing character index
         * in the modelChars array and if the word is less than 50 characters, it will be left padded with 0f.
         *
         * For example, if {@modelChars.lenght()} == 50, "hello" will be converted to
         * [
         *   0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
         *   0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,
         *   0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0, 40, 37, 44,
         *   44, 47
         *   ]
         *
         * which is a numerical input that neural network understand.
         */
        NDManager manager = ctx.getNDManager();

        if (input == null){
            return new NDList(manager.create(new float[maxLen]).expandDims(0));
        }

        int charArraySize = input.length();

//      special case
        if (charArraySize == 0 ){
            // return 0 filled array
            return new NDList(manager.create(new float[maxLen]).expandDims(0));
        }

        float[] finalResult = new float[maxLen];

        if(charArraySize > maxLen){
            /*
            Removing extra characters from the left
             */
            int startIdx = charArraySize - maxLen;
            int currentIdx = 0;
            int beginIndex = 0;
            for (Character charVal: input.toCharArray()){
                if(currentIdx < startIdx){
                    currentIdx++;
                    continue;
                }
                int charIdx = modelChars.indexOf(charVal);
                if(charIdx != -1){
                    finalResult[beginIndex] = (float) charIdx;
                } else {
                    finalResult[beginIndex] = 0f;
                }
                beginIndex++;
                currentIdx++;
            }
        } else if (charArraySize < maxLen) { // pad with 0s on the left
            int startIdx = maxLen - charArraySize;
            int currentIdx = 0;
            for (Character charVal: input.toCharArray()){
                if(currentIdx < startIdx){
                    finalResult[currentIdx] = 0f;
                } else {
                    int charIdx = modelChars.indexOf(charVal);
                    if(charIdx != -1){
                        finalResult[currentIdx] = (float) charIdx;
                    } else {
                        finalResult[currentIdx] = 0f;
                    }
                }
                currentIdx++;
            }

        } else { // same size of maxLen and input
            int currentIdx = 0;
            for (Character charVal: input.toCharArray()){
                int charIdx = modelChars.indexOf(charVal);
                if(charIdx != -1){
                    finalResult[currentIdx] = (float) charIdx;
                } else {
                    finalResult[currentIdx] = 0f;
                }
                currentIdx++;
            }
        }

        return new NDList(manager.create(finalResult).expandDims(0));
    }


    @Override
    public Boolean processOutput(TranslatorContext ctx, NDList list) {
        NDArray ndArray = list.singletonOrThrow();
        NDArray ndArray1 = ndArray.get(0).argMax();
        return ndArray1.toUint8Array()[0] == 1;
    }

    @Override
    public Batchifier getBatchifier() {
        return null;
    }
}