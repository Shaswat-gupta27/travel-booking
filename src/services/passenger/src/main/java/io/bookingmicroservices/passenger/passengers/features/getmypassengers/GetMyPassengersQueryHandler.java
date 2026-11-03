package io.bookingmicroservices.passenger.passengers.features.getmypassengers;

import buildingblocks.mediator.abstractions.queries.IQueryHandler;
import io.bookingmicroservices.passenger.data.mongo.documents.PassengerDocument;
import io.bookingmicroservices.passenger.data.mongo.repositories.PassengerReadRepository;
import io.bookingmicroservices.passenger.passengers.dtos.PassengerDto;
import io.bookingmicroservices.passenger.passengers.features.Mappings;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class GetMyPassengersQueryHandler implements IQueryHandler<GetMyPassengersQuery, List<PassengerDto>> {
    private final PassengerReadRepository passengerReadRepository;

    public GetMyPassengersQueryHandler(PassengerReadRepository passengerReadRepository) {
        this.passengerReadRepository = passengerReadRepository;
    }

    @Override
    public List<PassengerDto> handle(GetMyPassengersQuery query) {
        return passengerReadRepository.findAllByUserIdAndIsDeletedFalse(query.userId()).stream()
                .sorted(Comparator.comparing(PassengerDocument::getName, String.CASE_INSENSITIVE_ORDER))
                .map(Mappings::toPassengerDto)
                .toList();
    }
}
