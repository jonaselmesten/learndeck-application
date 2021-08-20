package model;

import com.google.gson.annotations.SerializedName;

/**
 * Class to handle embedded responses.
 * @param <T> Type of list.
 */
public class ListResponse<T> {

    @SerializedName("_embedded")
    T list;

    public T getList() {
        return list;
    }
}