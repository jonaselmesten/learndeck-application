package server;

enum Directories {

    STUDENT("student"),TEACHER("teacher"),ERROR("error_msg");

    public final String folderName;

    Directories(String folderName) {
        this.folderName = folderName;
    }
}
