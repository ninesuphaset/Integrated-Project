package sit.int202.kp2itbmshop.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.List;

@Data
public class PageDTO<T> {
    private List<T> content;
    private boolean first;
    private boolean last;
    private Integer totalPages;
    private Integer totalElements;
    private Integer size;
    private String sort;
    @JsonIgnore
    private Integer number;
    public Integer getPage(){
        return number;
    }

}
