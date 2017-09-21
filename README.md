TestListView
===================
Test object,include listview,connect and control view.<br>
* Author: Eli Chang<br>
* Corporation: UBI-TEK<br>
* Email: eliflichang@gmail.com<br>

***
---
___

```java

        /**
         * 更新录制时间
         *
         * @param msg
         */
        private void refreshRecordTime(Message msg) {
            if (msg != null) {
                //获取录制时间
                int time = msg.getData().getInt("time");
                //更新文字
                if (eRecordTime != null && eTakePhotoView != null) {
                    if (time >= 0 && time <= LONGEST_RECORD_TIME) {
                        String timeText = Util.formatTime(time);
                        eRecordTime.setText(timeText);
                    } else {
                        eRecordTime.setText("");
                    }
                }
            }
        }
        
```
![](https://github.com/BoboHezi/TestListView/raw/master/app/src/main/res/drawable/banner.png)<br>
