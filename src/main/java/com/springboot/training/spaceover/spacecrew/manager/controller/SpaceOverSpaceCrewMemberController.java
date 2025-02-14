package com.springboot.training.spaceover.spacecrew.manager.controller;

import com.github.fge.jsonpatch.JsonPatch;
import com.springboot.training.spaceover.spacecrew.manager.domain.model.SpaceCrewMember;
import com.springboot.training.spaceover.spacecrew.manager.domain.request.inbound.CreateSpaceCrewMemberRequest;
import com.springboot.training.spaceover.spacecrew.manager.domain.request.inbound.PutSpaceCrewMemberRequest;
import com.springboot.training.spaceover.spacecrew.manager.domain.response.outbound.GetSpaceCrewMemberResponse;
import com.springboot.training.spaceover.spacecrew.manager.domain.response.outbound.PatchSpaceCrewMemberResponse;
import com.springboot.training.spaceover.spacecrew.manager.domain.response.outbound.PutSpaceCrewMemberResponse;
import com.springboot.training.spaceover.spacecrew.manager.enums.SpaceCrewMemberRole;
import com.springboot.training.spaceover.spacecrew.manager.enums.SpaceCrewMemberStatus;
import com.springboot.training.spaceover.spacecrew.manager.service.SpaceCrewMemberService;
import com.springboot.training.spaceover.spacecrew.manager.utils.annotatations.ServiceOperation;
import com.springboot.training.spaceover.spacecrew.manager.utils.assemblers.PaginationModelAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.springboot.training.spaceover.spacecrew.manager.utils.constants.SpaceCrewManagerConstant.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(SPACE_CREW_MEMBERS_URI)
@Tag(name = SPACE_CREW_MEMBERS, description = SPACE_CREW_MEMBER_API_DESCRIPTION)
public class SpaceOverSpaceCrewMemberController extends SpaceOverGenericController implements SpaceCrewMemberController {

    private final SpaceCrewMemberService spaceCrewMemberService;

    private final ModelMapper modelMapper;

    private final PagedResourcesAssembler<SpaceCrewMember> pagedModelAssembler;

    private final PaginationModelAssembler modelAssembler;

    @Override
    @GetMapping
    @PageableAsQueryParam
    @ServiceOperation(GET_SPACE_CREW_MEMBERS_SERVICE_OPERATION)
    @Operation(summary = GET_SPACE_CREW_MEMBERS_SERVICE_OPERATION, description = GET_SPACE_CREW_MEMBERS_SERVICE_OPERATION_DESCRIPTION)
    public ResponseEntity<PagedModel<GetSpaceCrewMemberResponse>> getSpaceCrewMembers(@Parameter(hidden = true) Pageable pageable,
                                                                                      @RequestParam(name = NAME_FIELD, required = false) String name,
                                                                                      @RequestParam(name = STATUS_FIELD, required = false) String status,
                                                                                      @RequestParam(name = ROLE_FIELD, required = false) String role,
                                                                                      @RequestParam(name = SPACESHIP_ID_FIELD, required = false) Long spaceShipId) {
        log.trace(GET_SPACE_CREW_MEMBERS_REQUEST_MSG);
        SpaceCrewMember spaceMissionSample = SpaceCrewMember.builder()
                .name(name)
                .status(SpaceCrewMemberStatus.fromName(status))
                .role(SpaceCrewMemberRole.fromName(role))
                .spaceShipId(spaceShipId)
                .build();
        Page<SpaceCrewMember> spaceMissionPage = spaceCrewMemberService.findAll(spaceMissionSample, pageable);
        PagedModel<GetSpaceCrewMemberResponse> response = pagedModelAssembler.toModel(spaceMissionPage, modelAssembler);
        log.info(GET_SPACE_CREW_MEMBERS_RESULT_MSG);
        return ResponseEntity.ok(response);
    }

    @Override
    @GetMapping(ID_URI)
    @ServiceOperation(GET_SPACE_CREW_MEMBER_SERVICE_OPERATION)
    @Operation(summary = GET_SPACE_CREW_MEMBER_SERVICE_OPERATION, description = GET_SPACE_CREW_MEMBER_SERVICE_OPERATION_DESCRIPTION)
    public ResponseEntity<GetSpaceCrewMemberResponse> getSpaceCrewMember(@PathVariable("id") Long id) {
        log.trace(GET_SPACE_CREW_MEMBERS_REQUEST_MSG, id);
        GetSpaceCrewMemberResponse response = modelMapper.map(spaceCrewMemberService.findBydId(id), GetSpaceCrewMemberResponse.class);
        log.info(GET_SPACE_CREW_MEMBERS_RESULT_MSG, id);
        return ResponseEntity.ok(response);
    }

    @Override
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ServiceOperation(CREATE_SPACE_CREW_MEMBER_SERVICE_OPERATION)
    @Operation(summary = CREATE_SPACE_CREW_MEMBER_SERVICE_OPERATION, description = CREATE_SPACE_CREW_MEMBER_SERVICE_OPERATION_DESCRIPTION)
    public ResponseEntity createSpaceCrewMember(@RequestBody @Valid CreateSpaceCrewMemberRequest request) {
        log.trace(CREATE_SPACE_CREW_MEMBER_REQUEST_MSG);
        SpaceCrewMember spaceCrewMember = spaceCrewMemberService.save(modelMapper.map(request, SpaceCrewMember.class));
        log.info(CREATE_SPACE_CREW_MEMBER_RESULT_MSG, spaceCrewMember.getId());
        return ResponseEntity.created(getResourceUri(spaceCrewMember.getId())).build();
    }

    @Override
    @PatchMapping(ID_URI)
    @ServiceOperation(PATCH_SPACE_CREW_MEMBER_SERVICE_OPERATION)
    @Operation(summary = PATCH_SPACE_CREW_MEMBER_SERVICE_OPERATION, description = PATCH_SPACE_CREW_MEMBER_SERVICE_OPERATION_DESCRIPTION)
    public ResponseEntity<PatchSpaceCrewMemberResponse> patchSpaceMission(@PathVariable("id") Long id, @RequestBody JsonPatch patch) {
        log.trace(PATCH_SPACE_CREW_MEMBER_REQUEST_MSG, id);
        SpaceCrewMember entity = spaceCrewMemberService.findBydId(id);
        entity = spaceCrewMemberService.update(applyPatch(patch, entity));
        log.info(PATCH_SPACE_CREW_MEMBER_RESULT_MSG, id);
        return ResponseEntity.ok(modelMapper.map(entity, PatchSpaceCrewMemberResponse.class));
    }

    @Override
    @PutMapping(ID_URI)
    @ServiceOperation(PUT_SPACE_CREW_MEMBER_SERVICE_OPERATION)
    @Operation(summary = PUT_SPACE_CREW_MEMBER_SERVICE_OPERATION, description = PUT_SPACE_CREW_MEMBER_SERVICE_OPERATION_DESCRIPTION)
    public ResponseEntity<PutSpaceCrewMemberResponse> putSpaceMission(@PathVariable("id") Long id, @RequestBody @Valid PutSpaceCrewMemberRequest request) {
        log.trace(PUT_SPACE_CREW_MEMBER_REQUEST_MSG, id);
        request.setId(id);
        SpaceCrewMember entity = spaceCrewMemberService.update(modelMapper.map(request, SpaceCrewMember.class));
        log.info(PUT_SPACE_CREW_MEMBER_RESULT_MSG, id);
        return ResponseEntity.ok(modelMapper.map(entity, PutSpaceCrewMemberResponse.class));
    }

    @Override
    @DeleteMapping(ID_URI)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ServiceOperation(DELETE_SPACE_CREW_MEMBER_SERVICE_OPERATION)
    @Operation(summary = DELETE_SPACE_CREW_MEMBER_SERVICE_OPERATION, description = DELETE_SPACE_CREW_MEMBER_SERVICE_OPERATION_DESCRIPTION)
    public ResponseEntity deleteSpaceCrewMember(@PathVariable("id") Long id) {
        log.trace(DELETE_SPACE_CREW_MEMBER_REQUEST_MSG, id);
        spaceCrewMemberService.deleteById(id);
        log.info(DELETE_SPACE_CREW_MEMBER_RESULT_MSG, id);
        return ResponseEntity.noContent().build();
    }
}
