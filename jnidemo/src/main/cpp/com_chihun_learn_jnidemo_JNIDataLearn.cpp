//
// Created by zhenduowang on 2019/6/12.
//
#include "com_chihun_learn_jnidemo_JNIDataLearn.h"
#include <android/log.h>
#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <cstdlib>
#include <sstream>
//#include <platform.h>
//#include <uuid/uuid.h>
/*
  %a  浮点数、十六进制数字和p-记数法（c99
  %A  浮点数、十六进制数字和p-记法（c99）
  %c  一个字符(char)
  %C  一个ISO宽字符
  %d  有符号十进制整数(int)（%ld、%Ld：长整型数据(long),%hd：输出短整形。）　
  %e  浮点数、e-记数法
  %E  浮点数、E-记数法
  %f  单精度浮点数(默认float)、十进制记数法（%.nf  这里n表示精确到小数位后n位.十进制计数）
  %g  根据数值不同自动选择%f或%e．
  %G  根据数值不同自动选择%f或%e.
  %i  有符号十进制数（与%d相同）
  %o  无符号八进制整数
  %p  指针
  %s  对应字符串char*（%s = %hs = %hS 输出 窄字符）
  %S  对应宽字符串WCAHR*（%ws = %S 输出宽字符串）
  %u  无符号十进制整数(unsigned int)
  %x  使用十六进制数字0xf的无符号十六进制整数　
  %X  使用十六进制数字0xf的无符号十六进制整数
  %%  打印一个百分号
  %I64d 用于INT64 或者 long long
  %I64u 用于UINT64 或者 unsigned long long
  %I64x 用于64位16进制数据
  ---------------------
  参考文献：
  https://blog.csdn.net/xiexievv/article/details/6831194
  https://www.cnblogs.com/arxive/p/5160007.html
*/
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG , "JNILearn", __VA_ARGS__)

/*
 * Class:     com_chihun_learn_jnidemo_JNIDataLearn
 * Method:    getString
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_chihun_learn_jnidemo_JNIDataLearn_getString
  (JNIEnv *env, jobject thiz){
        return env->NewStringUTF("Hello World");
  }

/*
 * Class:     com_chihun_learn_jnidemo_JNIDataLearn
 * Method:    getInt
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_com_chihun_learn_jnidemo_JNIDataLearn_getInt
  (JNIEnv *env, jobject thiz) {
        jint value = 100;
        return value;
  }

/*
 * Class:     com_chihun_learn_jnidemo_JNIDataLearn
 * Method:    getLong
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_com_chihun_learn_jnidemo_JNIDataLearn_getLong
  (JNIEnv *env, jobject thiz) {
        jlong value = 10000000000L;
        return value;
  }

/*
 * Class:     com_chihun_learn_jnidemo_JNIDataLearn
 * Method:    getFloat
 * Signature: ()F
 */
JNIEXPORT jfloat JNICALL Java_com_chihun_learn_jnidemo_JNIDataLearn_getFloat
  (JNIEnv *env, jobject thiz) {
      jfloat value = 100.00F;
      return value;
  }

/*
 * Class:     com_chihun_learn_jnidemo_JNIDataLearn
 * Method:    getDouble
 * Signature: ()D
 */
JNIEXPORT jdouble JNICALL Java_com_chihun_learn_jnidemo_JNIDataLearn_getDouble
  (JNIEnv *env, jobject thiz) {
    jdouble value = 10000.000000;
    return value;
  }

/*
 * Class:     com_chihun_learn_jnidemo_JNIDataLearn
 * Method:    getChar
 * Signature: ()C
 */
JNIEXPORT jchar JNICALL Java_com_chihun_learn_jnidemo_JNIDataLearn_getChar
  (JNIEnv *env, jobject thiz) {
    jchar value = 'a';
    return value;
  }

/*
 * Class:     com_chihun_learn_jnidemo_JNIDataLearn
 * Method:    getByte
 * Signature: ()B
 */
JNIEXPORT jbyte JNICALL Java_com_chihun_learn_jnidemo_JNIDataLearn_getByte
  (JNIEnv *env, jobject thiz) {
     jbyte value = 48;
     return value;
}

/*
 * Class:     com_chihun_learn_jnidemo_JNIDataLearn
 * Method:    getBoolean
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_com_chihun_learn_jnidemo_JNIDataLearn_getBoolean
  (JNIEnv *env, jobject thiz) {
    jboolean value = true;
    return value;
  }

/*
 * Class:     com_chihun_learn_jnidemo_JNIDataLearn
 * Method:    getShort
 * Signature: ()S
 */
JNIEXPORT jshort JNICALL Java_com_chihun_learn_jnidemo_JNIDataLearn_getShort
  (JNIEnv *env, jobject thiz) {
    jshort value = 123456;
    return value;
  }

/*
 * Class:     com_chihun_learn_jnidemo_JNIDataLearn
 * Method:    setString
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_chihun_learn_jnidemo_JNIDataLearn_setString
  (JNIEnv *env, jobject thiz, jstring value) {
    const char *utfWord = env->GetStringUTFChars(value, JNI_FALSE); // JNI_FALSE JNI_TRUE NULL
    jsize utfLen = env->GetStringUTFLength(value);
    LOGD("utf string from java: %s %d", utfWord, utfLen);
    const jchar *word = env->GetStringChars(value, 0);
    jsize len = env->GetStringLength(value);
    LOGD("string from java: %s %d", word, len);
    env->ReleaseStringUTFChars(value, utfWord);
    env->ReleaseStringChars(value, word);
  }

/*
 * Class:     com_chihun_learn_jnidemo_JNIDataLearn
 * Method:    setInt
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_com_chihun_learn_jnidemo_JNIDataLearn_setInt
  (JNIEnv *env, jobject thiz, jint value) {
    LOGD("int from java: %d", value);
  }

/*
 * Class:     com_chihun_learn_jnidemo_JNIDataLearn
 * Method:    setLong
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_com_chihun_learn_jnidemo_JNIDataLearn_setLong
  (JNIEnv *env, jobject thiz, jlong value) {
  LOGD("long from java: %ld", value);
  }

/*
 * Class:     com_chihun_learn_jnidemo_JNIDataLearn
 * Method:    setFloat
 * Signature: (F)V
 */
JNIEXPORT void JNICALL Java_com_chihun_learn_jnidemo_JNIDataLearn_setFloat
  (JNIEnv *env, jobject thiz, jfloat value) {
  LOGD("float from java: %f", value);
  }

/*
 * Class:     com_chihun_learn_jnidemo_JNIDataLearn
 * Method:    setDouble
 * Signature: (D)V
 */
JNIEXPORT void JNICALL Java_com_chihun_learn_jnidemo_JNIDataLearn_setDouble
  (JNIEnv *env, jobject thiz, jdouble value) {
      LOGD("dounle from java: %lf", value);
  }

/*
 * Class:     com_chihun_learn_jnidemo_JNIDataLearn
 * Method:    setChar
 * Signature: (C)V
 */
JNIEXPORT void JNICALL Java_com_chihun_learn_jnidemo_JNIDataLearn_setChar
  (JNIEnv *env, jobject thiz, jchar value) {
  LOGD("char from java: %c", value);
  }

/*
 * Class:     com_chihun_learn_jnidemo_JNIDataLearn
 * Method:    setByte
 * Signature: (B)V
 */
JNIEXPORT void JNICALL Java_com_chihun_learn_jnidemo_JNIDataLearn_setByte
  (JNIEnv *env, jobject thiz, jbyte value) {
  LOGD("byte from java: %x", value);
  }

/*
 * Class:     com_chihun_learn_jnidemo_JNIDataLearn
 * Method:    setBoolean
 * Signature: (Z)V
 */
JNIEXPORT void JNICALL Java_com_chihun_learn_jnidemo_JNIDataLearn_setBoolean
  (JNIEnv *env, jobject thiz, jboolean value) {
  LOGD("boolean from java: %d", value);
  }

/*
 * Class:     com_chihun_learn_jnidemo_JNIDataLearn
 * Method:    setShort
 * Signature: (S)V
 */
JNIEXPORT void JNICALL Java_com_chihun_learn_jnidemo_JNIDataLearn_setShort
  (JNIEnv *env, jobject thiz, jshort value) {
  LOGD("short from java: %d", value);
  }

/*
 * Class:     com_chihun_learn_jnidemo_JNIDataLearn
 * Method:    setStringArray
 * Signature: ([Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_chihun_learn_jnidemo_JNIDataLearn_setStringArray
  (JNIEnv *env, jobject thiz, jobjectArray stringArray) {
       int i, len = 0;
       int array_len = env->GetArrayLength(stringArray);
       for (i=0; i<array_len; i++) {
           jstring jword = (jstring)env->GetObjectArrayElement(stringArray, i);
           len = env->GetStringUTFLength(jword);
           const char *word = env->GetStringUTFChars(jword, 0);
           LOGD("%s | %d", word, len);
       }
  }

/*
 * Class:     com_chihun_learn_jnidemo_JNIDataLearn
 * Method:    getStringArray
 * Signature: ()[Ljava/lang/String;
 */
JNIEXPORT jobjectArray JNICALL Java_com_chihun_learn_jnidemo_JNIDataLearn_getStringArray
  (JNIEnv *env, jobject thiz) {
    //一维数组
    jclass strArrCls = env->FindClass("java/lang/String"); // should be of the form 'package/Class', [Lpackage/Class;' or '[[B'
    //创建一个有2个元素数组
    jobjectArray strArr = env->NewObjectArray(2, strArrCls, NULL);
    //给以维数据填充值
    jobject str = env->NewStringUTF("Hello");
    env->SetObjectArrayElement(strArr, 0, str);
    jobject str2 = env->NewStringUTF("world");
    env->SetObjectArrayElement(strArr, 1, str);

    return strArr;
  }

/*
 * Class:     com_chihun_learn_jnidemo_JNIDataLearn
 * Method:    setIntArray
 * Signature: ([I)V
 */
JNIEXPORT void JNICALL Java_com_chihun_learn_jnidemo_JNIDataLearn_setIntArray
  (JNIEnv *env, jobject thiz, jintArray jarr) {
    //1.获取数组指针
      jint *arr = env->GetIntArrayElements(jarr, NULL);
      //2.获取数组长度
      int len = env->GetArrayLength(jarr);
      for(int i = 0; i < len; i++) {
        LOGD("intArray: %d", arr[i]);
      }

      //4.释放资源
      env->ReleaseIntArrayElements(jarr, arr, JNI_COMMIT);
    //    env->ReleaseIntArrayElements(jarr, arr, JNI_ABORT);
      //  对于最后一个参数(如果指针指向的数组为副本时，否则该参数不起作用)
      //      0       copy back the content and free the elems buffer
      //      JNI_COMMIT      copy back the content but do not free the elems buffer
      //      JNI_ABORT       free the buffer without copying back the possible changes
  }

/*
 * Class:     com_chihun_learn_jnidemo_JNIDataLearn
 * Method:    getIntArray
 * Signature: ()[I
 */
JNIEXPORT jintArray JNICALL Java_com_chihun_learn_jnidemo_JNIDataLearn_getIntArray
  (JNIEnv *env, jobject thiz) {
    //1.新建长度len数组
      jint len = 10;
      jintArray jarr = env->NewIntArray(len);
      //2.获取数组指针
      jint *arr = env->GetIntArrayElements(jarr, NULL);
      //3.赋值
      int i = 0;
      for(; i < len; i++){
          arr[i] = i;
      }
      //4.释放资源
      env->ReleaseIntArrayElements(jarr, arr, 0);
      //5.返回数组
      return jarr;
  }

/*
 * Class:     com_chihun_learn_jnidemo_JNIDataLearn
 * Method:    setLongArray
 * Signature: ([J)V
 */
JNIEXPORT void JNICALL Java_com_chihun_learn_jnidemo_JNIDataLearn_setLongArray
  (JNIEnv *env, jobject thiz, jlongArray jarr) {
        //1.获取数组指针
        jlong *arr = env->GetLongArrayElements(jarr, NULL);
        //2.获取数组长度
        int len = env->GetArrayLength(jarr);
        for(int i = 0; i < len; i++) {
          LOGD("longArray: %ld", arr[i]);
        }

        //4.释放资源
        env->ReleaseLongArrayElements(jarr, arr, JNI_COMMIT);
  }

/*
 * Class:     com_chihun_learn_jnidemo_JNIDataLearn
 * Method:    getLongArray
 * Signature: ()[J
 */
JNIEXPORT jlongArray JNICALL Java_com_chihun_learn_jnidemo_JNIDataLearn_getLongArray
  (JNIEnv *env, jobject thiz) {
    //1.新建长度len数组
        jint len = 10;
        jlongArray jarr = env->NewLongArray(len);
        //2.获取数组指针
        jlong *arr = env->GetLongArrayElements(jarr, NULL);
        //3.赋值
        int i = 0;
        for(; i < len; i++){
            arr[i] = i * 100;
        }
        //4.释放资源
        env->ReleaseLongArrayElements(jarr, arr, 0);
        //5.返回数组
        return jarr;
  }

  /*
   * Class:     com_chihun_learn_jnidemo_JNIDataLearn
   * Method:    getString2DArray
   * Signature: ()[[Ljava/lang/String;
   */
  JNIEXPORT jobjectArray JNICALL Java_com_chihun_learn_jnidemo_JNIDataLearn_getString2DArray
    (JNIEnv *env, jobject thiz) {
        // should be of the form 'package/Class', [Lpackage/Class;' or '[[B'
        jclass strArrCls = env->FindClass("[Ljava/lang/String;");
        //创建一个有2个元素,每个元素是数组的数组
        jobjectArray str2DArr = env->NewObjectArray(2, strArrCls, NULL);

        jclass strCls = env->FindClass("java/lang/String");
        //创建一个有3个元素的数组
        jobjectArray strArr = env->NewObjectArray(3, strCls, NULL);
        //给一维数据填充值
        jobject str = env->NewStringUTF("Hello");
        env->SetObjectArrayElement(strArr, 0, str);
        jobject str2 = env->NewStringUTF("world");
        env->SetObjectArrayElement(strArr, 1, str2);
        jobject str3 = env->NewStringUTF("!");
        env->SetObjectArrayElement(strArr, 2, str3);
        env->SetObjectArrayElement(str2DArr, 0, strArr);

        //创建一个有3个元素的数组
        jobjectArray strArr2 = env->NewObjectArray(3, strCls, NULL);
        //给一维数据填充值
        jobject str21 = env->NewStringUTF("ni");
        env->SetObjectArrayElement(strArr2, 0, str21);
        jobject str22 = env->NewStringUTF("hao");
        env->SetObjectArrayElement(strArr2, 1, str22);
        jobject str23 = env->NewStringUTF("ya!");
        env->SetObjectArrayElement(strArr2, 2, str23);
        env->SetObjectArrayElement(str2DArr, 1, strArr2);

        return str2DArr;
   }

  /*
   * Class:     com_chihun_learn_jnidemo_JNIDataLearn
   * Method:    getPersonArray
   * Signature: ()[Lcom/ubtechinc/cruzr/jnilearn/entity/Person;
   */
  JNIEXPORT jobjectArray JNICALL Java_com_chihun_learn_jnidemo_JNIDataLearn_getPersonArray
    (JNIEnv *env, jobject thiz) {
        jobjectArray infos = NULL;	// jobjectArray 为指针类型
        jclass clsPerson = NULL;		// jclass 为指针类型
        jobject obj;
        jfieldID nameID;
        jfieldID ageID;
        jfieldID maleID;
        jmethodID consID;
        jsize len;
        int i;

        clsPerson = env->FindClass("com/chihun/learn/jnidemo/entity/Person");
        len = 5;
        infos = env->NewObjectArray(len, clsPerson, NULL);
        nameID = env->GetFieldID(clsPerson, "name", "Ljava/lang/String;");
        ageID = env->GetFieldID(clsPerson, "age", "I");
        maleID = env->GetFieldID(clsPerson, "male", "Z");
        consID = env->GetMethodID(clsPerson, "<init>", "()V");
        for(i = 0; i < len; i++) {
            obj = env->NewObject(clsPerson, consID);
            char name[80];
            strcpy(name,"name_");
            char s[10];
            s[0] = '0' + i;
            strcat(name, s);
            jstring nameStr = env->NewStringUTF(name);
            env->SetObjectField(obj, nameID, nameStr);
            env->SetIntField(obj, ageID, (jint)(i + 20));
            env->SetBooleanField(obj, maleID, i % 2 == 0 ? JNI_TRUE : JNI_FALSE);
            env->SetObjectArrayElement(infos, i, obj);
        }
        return infos;
    }

  /*
   * Class:     com_chihun_learn_jnidemo_JNIDataLearn
   * Method:    getPerson
   * Signature: ()Lcom/ubtechinc/cruzr/jnilearn/entity/Person;
   */
  JNIEXPORT jobject JNICALL Java_com_chihun_learn_jnidemo_JNIDataLearn_getPerson
    (JNIEnv *env, jobject thiz) {
        jclass clsPerson = NULL; // jclass 为指针类型
        jobject obj;
        jfieldID nameID;
        jfieldID ageID;
        jfieldID maleID;
        jmethodID consID;

        clsPerson = env->FindClass("com/chihun/learn/jnidemo/entity/Person");
        nameID = env->GetFieldID(clsPerson, "name", "Ljava/lang/String;");
        ageID = env->GetFieldID(clsPerson, "age", "I");
        maleID = env->GetFieldID(clsPerson, "male", "Z");
        consID = env->GetMethodID(clsPerson, "<init>", "()V");

        obj = env->NewObject(clsPerson, consID);
        env->SetObjectField(obj, nameID, env->NewStringUTF("bruce"));
        env->SetIntField(obj, ageID, (jint)20);
        env->SetBooleanField(obj, maleID, JNI_TRUE);
        return obj;
    }

  /*
  std::string uuid()
  {
      UUID uuid;
      //The UUID is 16 bytes (128 bits) long
      uuid_generate(reinterpret_cast<unsigned char *>(&uuid));
      char buf[64] = {0};
      snprintf(buf, sizeof(buf), "%08x-%04x-%04x-%02x%02x-%02x%02x%02x%02x%02x%02x", uuid.data1, uuid.data2, uuid.data3,
          uuid.data4[0], uuid.data4[1],uuid.data4[2],uuid.data4[3],
          uuid.data4[4],uuid.data4[5],uuid.data4[6],uuid.data4[7]);
      return std::string(buf);
  }

  std::string convertToString(double d) {
      std::ostringstream os;
      if (os << d)
          return os.str();
      return "invalid conversion";
  }

  const char* string2Char(std::string str) {
      return str.c_str();
  }

  jstring str2jstring(JNIEnv* env,const char* pat)
  {
      //定义java String类 strClass
      jclass strClass = (env)->FindClass("java/lang/String");
      //获取String(byte[],String)的构造器,用于将本地byte[]数组转换为一个新String
      jmethodID ctorID = (env)->GetMethodID(strClass, "<init>", "([BLjava/lang/String;)V");
      //建立byte数组
      jbyteArray bytes = (env)->NewByteArray(strlen(pat));
      //将char* 转换为byte数组
      (env)->SetByteArrayRegion(bytes, 0, strlen(pat), (jbyte*)pat);
      // 设置String, 保存语言类型,用于byte数组转换至String时的参数
      jstring encoding = (env)->NewStringUTF("GB2312");
      //将byte数组转换为java String,并输出
      return (jstring)(env)->NewObject(strClass, ctorID, bytes, encoding);
  }

  std::string jstring2str(JNIEnv* env, jstring jstr)
  {
      char*   rtn   =   NULL;
      jclass   clsstring   =   env->FindClass("java/lang/String");
      jstring   strencode   =   env->NewStringUTF("GB2312");
      jmethodID   mid   =   env->GetMethodID(clsstring,   "getBytes",   "(Ljava/lang/String;)[B");
      jbyteArray   barr=   (jbyteArray)env->CallObjectMethod(jstr,mid,strencode);
      jsize   alen   =   env->GetArrayLength(barr);
      jbyte*   ba   =   env->GetByteArrayElements(barr,JNI_FALSE);
      if(alen   >   0)
      {
          rtn   =   (char*)malloc(alen+1);
          memcpy(rtn,ba,alen);
          rtn[alen]=0;
      }
      env->ReleaseByteArrayElements(barr,ba,0);
      std::string stemp(rtn);
      free(rtn);
      return   stemp;
  }
  */