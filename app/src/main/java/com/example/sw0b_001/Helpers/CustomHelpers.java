package com.example.sw0b_001.Helpers;

import com.example.sw0b_001.R;

public class CustomHelpers {
    public static int getLetterImage(char letter) {
        int image = R.drawable.ic_round_message_24;
        switch (letter) {
            case 'i':
                image = R.mipmap.letter_i;
            break;

            case 's':
                image = R.mipmap.letter_s;
            break;
        }
        return image;
    }
}
