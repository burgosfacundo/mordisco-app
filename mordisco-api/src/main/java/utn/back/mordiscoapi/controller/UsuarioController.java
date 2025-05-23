package utn.back.mordiscoapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utn.back.mordiscoapi.exception.BadRequestException;
import utn.back.mordiscoapi.exception.NotFoundException;
import utn.back.mordiscoapi.model.dto.usuario.UsuarioDTO;
import utn.back.mordiscoapi.model.dto.usuario.UsuarioUpdateDTO;
import utn.back.mordiscoapi.model.projection.UsuarioProjection;
import utn.back.mordiscoapi.service.impl.UsuarioServiceImpl;

import java.util.List;

@Tag(name = "Usuarios", description = "Operaciones relacionadas a usuarios")
@RestController
@RequestMapping("/api/usuario")
@RequiredArgsConstructor
public class UsuarioController {
    private final UsuarioServiceImpl service;

    /**
     * Función para guardar un nuevo usuario.
     * @param dto Objeto DTO que contiene los datos del usuario a crear.
     * @return Respuesta HTTP con un mensaje de éxito.
     * @throws BadRequestException Si hay un error en los datos proporcionados.
     */
    @Operation(summary = "Crear un usuario nuevo", description = "Recibe un usuario y lo guarda en la base de datos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Promoción creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Error en los datos proporcionados"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping("/save")
    public ResponseEntity<String> save(@RequestBody
                                       @Valid
                                       UsuarioDTO dto) throws BadRequestException {
        service.save(dto);
        return ResponseEntity.ok("Usuario guardado correctamente");
    }

    /**
     * Función para obtener todos los usuarios.
     * @return Respuesta HTTP con una lista de proyecciones de usuarios.
     */
    @Operation(summary = "Obtener todos los usuarios", description = "Devuelve una lista con todos los usuarios")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Promoción creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Error en los datos proporcionados"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public ResponseEntity<List<UsuarioProjection>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    /**
     * Función para obtener un usuario por su ID.
     * @param id del usuario a buscar.
     * @return Respuesta HTTP con la proyección del usuario encontrado.
     * @throws NotFoundException Si no se encuentra el usuario con el ID proporcionado.
     */
    @Operation(summary = "Obtener un usuario por ID", description = "Recibe un ID y devuelve el usuario correspondiente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Devuelve el usuario correspondiente"),
            @ApiResponse(responseCode = "404", description = "No se encontró el usuario con el ID proporcionado"),
            @ApiResponse(responseCode = "400", description = "Error en los datos proporcionados"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioProjection> findById(@PathVariable
                                                        Long id) throws NotFoundException {
        return ResponseEntity.ok(service.findById(id));
    }

    /**
     * Función para actualizar un usuario por su ID.
     * @param id del usuario a actualizar.
     * @param dto Objeto DTO que contiene los nuevos datos del usuario.
     * @return Respuesta HTTP con un mensaje de éxito.
     * @throws NotFoundException Si no se encuentra el usuario con el ID proporcionado.
     * @throws BadRequestException Si hay un error en los datos proporcionados.
     */
    @Operation(summary = "Actualizar un usuario", description = "Recibe un ID y un usuario y actualiza el usuario correspondiente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Usuario actualizada exitosamente"),
            @ApiResponse(responseCode = "404", description = "No se encontró el usuario con el ID proporcionado"),
            @ApiResponse(responseCode = "400", description = "Error en los datos proporcionados"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping("/{id}")
    public ResponseEntity<String> update(@PathVariable
                                         Long id,
                                         @RequestBody
                                         @Valid
                                         UsuarioUpdateDTO dto) throws NotFoundException, BadRequestException {
        service.update(id,dto);
        return ResponseEntity.ok().body("Usuario actualizado exitosamente");
    }

    /**
     * Función para eliminar un usuario por su ID.
     * @param id del usuario a eliminar.
     * @return Respuesta HTTP con un mensaje de éxito.
     * @throws NotFoundException Si no se encuentra el usuario con el ID proporcionado.
     */
    @Operation(summary = "Eliminar un usuario", description = "Recibe un ID y elimina el usuario correspondiente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Usuario eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "No se encontró el usuario con el ID proporcionado"),
            @ApiResponse(responseCode = "400", description = "Error en los datos proporcionados"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable
                                         Long id) throws NotFoundException {
        service.delete(id);
        return ResponseEntity.ok().body("Usuario eliminado exitosamente");
    }

    /**
     * Función para actualizar la contraseña de un usuario por su ID.
     * @param id del usuario a actualizar.
     * @param oldPassword contraseña antigua del usuario.
     * @param newPassword contraseña nueva del usuario
     * @return Respuesta HTTP con un mensaje de éxito.
     * @throws NotFoundException Si no se encuentra el usuario con el ID proporcionado.
     * @throws BadRequestException Si hay un error en los datos proporcionados.
     */
    @Operation(summary = "Actualizar la contraseña de un usuario", description = "Recibe un ID, la vieja contraseña y la nueva contraseña y actualiza el usuario correspondiente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Usuario actualizada exitosamente"),
            @ApiResponse(responseCode = "404", description = "No se encontró el usuario con el ID proporcionado"),
            @ApiResponse(responseCode = "400", description = "Error en los datos proporcionados"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping("/password/{id}")
    public ResponseEntity<String> changePassword(
                                         @RequestBody
                                         @Valid
                                         @PathVariable
                                         Long id,
                                         String oldPassword,
                                         String newPassword) throws NotFoundException, BadRequestException {
        service.changePassword(oldPassword,newPassword,id);
        return ResponseEntity.ok().body("Contraseña actualizada exitosamente");
    }

    /**
     * Función para obtener todos los usuarios por IDROL.
     * @param id del rol a buscar.
     * @return Respuesta HTTP con una lista de proyecciones de usuarios.
     */
    @Operation(summary = "Obtener todos los usuarios por rol", description = "Devuelve una lista con todos los usuarios")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Promoción creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Error en los datos proporcionados"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/rol/{id}")
    public ResponseEntity<List<UsuarioProjection>> findAllByRol(
                                                            @PathVariable
                                                            Long id
    ) {
        return ResponseEntity.ok(service.findByProjectRol(id));
    }
}
