namespace java com.throwsnew.thriftdemo.api
service HelloService{
 string helloString(1:string para)
 i32 helloInt(1:i32 para)
 bool helloBoolean(1:bool para)
 void helloVoid()
 string helloNull()
}