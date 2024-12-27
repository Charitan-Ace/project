package ace.charitan.project.internal.dto.country;

import java.io.Serializable;

import ace.charitan.project.internal.utils.DtoWithProcessId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class GetCountryByCountryCode {
    public static class GetCountryByCountryCodeRequestDto extends DtoWithProcessId {

        private String countryCode;

        public GetCountryByCountryCodeRequestDto() {

        }

        public GetCountryByCountryCodeRequestDto(String correlationId, String countryCode) {
            super(correlationId);
            this.countryCode = countryCode;
        }

        public String countryCode() {
            return countryCode;
        }

    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public class GetCountryByCountryCodeResponseDto implements Serializable {

        private String correlationId;

        private String isoCode;

        private String regionName;
    }

}
