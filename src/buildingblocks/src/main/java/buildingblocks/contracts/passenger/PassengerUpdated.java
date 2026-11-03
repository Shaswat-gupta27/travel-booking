package buildingblocks.contracts.passenger;

import buildingblocks.core.event.IntegrationEvent;

import java.util.UUID;

public record PassengerUpdated(UUID Id) implements IntegrationEvent {
}
