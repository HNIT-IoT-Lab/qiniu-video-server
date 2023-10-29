package cn.hnit.sdk.orm.mongodb.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Sort;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SortOperationMapping {

    private Sort.Direction sort;

    private String orderBy;
}
