package kingsley.rvlibrary;

/**
 * class name : Timer
 * author : Kingsley
 * created date : on 2017/7/24 21:20
 * file change date : on 2017/7/24 21:20
 * version: 1.0
 */

public interface Constant {
    int HEADER_TYPE = 100000;
    int FOOTER_TYPE = 200000;
    int EMPTY_TYPE = Integer.MAX_VALUE -1;
    int LOADING_TYPE = Integer.MAX_VALUE -2;

    int DEFAULT_STATUS = 1;
    int LOADING_STATUS = 2;
    int FAILED_STATUS = 3;
    int END_STATUS = 4;
}
