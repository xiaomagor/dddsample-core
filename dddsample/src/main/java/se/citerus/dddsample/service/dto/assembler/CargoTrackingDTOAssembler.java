package se.citerus.dddsample.service.dto.assembler;

import se.citerus.dddsample.domain.*;
import se.citerus.dddsample.service.dto.CargoTrackingDTO;
import se.citerus.dddsample.service.dto.HandlingEventDTO;

import java.util.List;

/**
 * Assembler class for the CargoTrackingDTO.
 */
public class CargoTrackingDTOAssembler {

  public CargoTrackingDTO toDTO(final Cargo cargo) {
    final DeliveryHistory deliveryHistory = cargo.deliveryHistory();
    final Location currentLocation = deliveryHistory.currentLocation();
    final CarrierMovement currentCarrierMovement = deliveryHistory.currentCarrierMovement();
    final CargoTrackingDTO dto = new CargoTrackingDTO(
      cargo.trackingId().idString(),
      cargo.origin().toString(),
      cargo.finalDestination().toString(),
      deliveryHistory.status(),
      currentLocation == null ? null : currentLocation.unLocode().idString(),
      currentCarrierMovement == null ? null : currentCarrierMovement.carrierMovementId().idString(),
      cargo.isMisdirected()
    );

    final List<HandlingEvent> events = deliveryHistory.eventsOrderedByCompletionTime();
    for (HandlingEvent event : events) {
      final CarrierMovement cm = event.carrierMovement();
      final String carrierIdString = (cm == null) ? "" : cm.carrierMovementId().idString();
      dto.addEvent(toHandlingEventDTO(cargo, event, carrierIdString));
    }
    return dto;
  }

  protected HandlingEventDTO toHandlingEventDTO(final Cargo cargo, HandlingEvent event, String carrierIdString) {
    return new HandlingEventDTO(
      event.location().toString(),
      event.type().toString(),
      carrierIdString,
      event.completionTime(),
      cargo.itinerary().isExpected(event)
    );
  }
}