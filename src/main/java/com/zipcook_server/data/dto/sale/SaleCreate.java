package com.zipcook_server.data.dto.sale;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Date;


@Data
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SaleCreate {


    private String username;


    private String nickname;

    @NotBlank(message = "제목을 입력하세요")
    @Size(max=30)
    private String title;


    private String place;

    private String price;

    private String discountPrice;

    private Date regDate;


}
