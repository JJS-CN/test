package com.example.admin.test.expandable;

import java.util.List;

/**
 * 说明：
 * Created by jjs on 2018/11/9.
 */

public class UserDataEntity {

    /**
     * code : 000000
     * message : 成功
     * result : [{"id":"100211","user_name":"阿飞","avatar":"http://thirdwx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTJEMrRUnBSh2cic3BwiaZ3wiab0qPOLjTiaRcJrCwWCVN7pkiaqgKO4kFk5QXj6EplpDwtEZvZiaH2icDr3w/132","children":[{"id":"100212","user_name":"15625732879","avatar":null,"children":[{"id":"100208","user_name":"18575518402","avatar":null}]}]},{"id":"100213","user_name":"13192051428","avatar":null,"children":[]}]
     */

    public String code;
    public String message;
    public List<ResultEntityA> result;

    //继承于ExpandableItem
    public static class ResultEntityA extends ExpandableItem {
        /**
         * id : 100211
         * user_name : 阿飞
         * avatar : http://thirdwx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTJEMrRUnBSh2cic3BwiaZ3wiab0qPOLjTiaRcJrCwWCVN7pkiaqgKO4kFk5QXj6EplpDwtEZvZiaH2icDr3w/132
         * children : [{"id":"100212","user_name":"15625732879","avatar":null,"children":[{"id":"100208","user_name":"18575518402","avatar":null}]}]
         */

        public String id;
        public String user_name;
        public String avatar;
        public List<ResultEntityB> children;

        @Override
        public int getItemType() {
            return 0;
        }

        @Override
        public boolean canSwitch() {
            return true;
        }

        @Override
        public List<? extends Expandable> getSubItems() {
            return children;
        }


        public static class ResultEntityB extends ExpandableItem {
            /**
             * id : 100212
             * user_name : 15625732879
             * avatar : null
             * children : [{"id":"100208","user_name":"18575518402","avatar":null}]
             */

            public String id;
            public String user_name;
            public Object avatar;
            public List<ResultEntityC> children;

            @Override
            public int getItemType() {
                return 1;
            }

            @Override
            public boolean canSwitch() {
                return true;
            }

            @Override
            public List<? extends Expandable> getSubItems() {
                return children;
            }


            public static class ResultEntityC extends ExpandableItem {
                /**
                 * id : 100208
                 * user_name : 18575518402
                 * avatar : null
                 */

                public String id;
                public String user_name;
                public Object avatar;

                @Override
                public int getItemType() {
                    return 2;
                }

                @Override
                public boolean canSwitch() {
                    return false;
                }

                @Override
                public List<? extends ExpandableItem> getSubItems() {
                    return null;
                }
            }
        }
    }
}
