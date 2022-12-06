package bci.challenge.mapper;

import bci.challenge.dto.PhoneDTO;
import bci.challenge.entity.Phone;
import org.springframework.stereotype.Component;

@Component
public class PhoneMapper {

    public Phone toEntity(PhoneDTO dto) {
        Phone phone = new Phone();
        phone.setNumber(dto.getNumber());
        phone.setCityCode(dto.getCityCode());
        phone.setCountryCode(dto.getCountryCode());
        return phone;
    }

    public PhoneDTO toDto(Phone phone) {
        PhoneDTO dto = new PhoneDTO();
        dto.setNumber(phone.getNumber());
        dto.setCityCode(phone.getCityCode());
        dto.setCountryCode(phone.getCountryCode());
        return dto;
    }

}
