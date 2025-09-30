package sit.int202.kp2itbmshop;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.MappingContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import sit.int202.kp2itbmshop.utils.ListMapper;

@SpringBootApplication
public class Kp2ItbMshopApplication {

    public static void main(String[] args) {
        SpringApplication.run(Kp2ItbMshopApplication.class, args);
    }

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        Converter<String, String> trimAndNullConverter = new Converter<String, String>() {
            @Override
            public String convert(MappingContext<String, String> context) {
                String source = context.getSource();
                if (source == null) return null;

                String trimmed = source.trim();
                return trimmed.isEmpty() ? null : trimmed;
            }
        };
        modelMapper.typeMap(String.class, String.class).setConverter(trimAndNullConverter);
        return modelMapper;
    }

    @Bean
    public ListMapper listMapper() {
        return ListMapper.getInstance();
    }
}
