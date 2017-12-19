package com.hardskins.hardskins.supportiveclasses;


public abstract class Vibration {

    protected long[] patternForVibrate(int numVibrate, int timeVibrate) {

        long[] pattern;

        switch (numVibrate) {
            case 1:
                switch (timeVibrate) {
                    case 1:
                        pattern = new long[]{333, 200};
                        break;
                    case 2:
                        pattern = new long[]{333, 400};
                        break;
                    case 3:
                        pattern = new long[]{333, 600};
                        break;
                    default:
                        pattern = new long[]{333, 400};
                        break;
                }
                break;

            case 2:
                switch (timeVibrate) {
                    case 1:
                        pattern = new long[]{333, 200, 333, 200};
                        break;
                    case 2:
                        pattern = new long[]{333, 400, 333, 444};
                        break;
                    case 3:
                        pattern = new long[]{333, 600, 333, 600};
                        break;
                    default:
                        pattern = new long[]{333, 400, 333, 444};
                        break;
                }
                break;


            case 3:
                switch (timeVibrate) {
                    case 1:
                        pattern = new long[]{333, 200, 333, 200, 333, 200};
                        break;
                    case 2:
                        pattern = new long[]{333, 400, 333, 400, 333, 400};
                        break;
                    case 3:
                        pattern = new long[]{333, 600, 333, 600, 333, 600};
                        break;
                    default:
                        pattern = new long[]{333, 400, 333, 400, 333, 400};
                        break;
                }
                break;

            case 4:
                switch (timeVibrate) {
                    case 1:
                        pattern = new long[]{333, 200, 333, 200, 333, 200, 333, 200};
                        break;
                    case 2:
                        pattern = new long[]{333, 400, 333, 400, 333, 400, 333, 400};
                        break;
                    case 3:
                        pattern = new long[]{333, 600, 333, 600, 333, 600, 333, 600};
                        break;
                    default:
                        pattern = new long[]{333, 400, 333, 400, 333, 400, 333, 400};
                        break;
                }
                break;


            case 5:
                switch (timeVibrate) {
                    case 1:
                        pattern = new long[]{333, 200, 333, 200, 333, 200, 333, 200, 333, 200};
                        break;
                    case 2:
                        pattern = new long[]{333, 400, 333, 400, 333, 400, 333, 400, 333, 400};
                        break;
                    case 3:
                        pattern = new long[]{333, 600, 333, 600, 333, 600, 333, 600, 333, 600};
                        break;
                    default:
                        pattern = new long[]{333, 400, 333, 400, 333, 400, 333, 400, 333, 400};
                        break;
                }
                break;

            default:
                switch (timeVibrate) {
                    case 1:
                        pattern = new long[]{333, 200, 333, 200};
                        break;
                    case 2:
                        pattern = new long[]{333, 400, 333, 444};
                        break;
                    case 3:
                        pattern = new long[]{333, 600, 333, 600};
                        break;
                    default:
                        pattern = new long[]{333, 400, 333, 444};
                        break;
                }
                break;

        }

        return pattern;
    }

}
