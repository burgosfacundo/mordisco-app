package utn.back.mordiscoapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import utn.back.mordiscoapi.exception.BadRequestException;
import utn.back.mordiscoapi.exception.NotFoundException;
import utn.back.mordiscoapi.model.dto.usuario.ChangePasswordDTO;
import utn.back.mordiscoapi.model.dto.usuario.UsuarioCreateDTO;
import utn.back.mordiscoapi.model.dto.usuario.UsuarioResponseDTO;
import utn.back.mordiscoapi.model.dto.usuario.UsuarioUpdateDTO;
import utn.back.mordiscoapi.service.interf.IUsuarioService;

import java.util.List;

@Tag(name = "Usuarios", description = "Operaciones relacionadas a usuarios")
@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {
    private final IUsuarioService service;

    /**
     * Función para guardar un nuevo usuario.
     * @param dto Objeto DTO que contiene los datos del usuario a crear.
     * @return Respuesta HTTP con un mensaje de éxito.
     * @throws NotFoundException Si no se encuentra el rol del usuario.
     */
    @Operation(summary = "Crear un usuario nuevo",
            description = "Recibe un usuario y lo guarda en la base de datos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",description = "Promoción creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Error en los datos proporcionados"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping("/save")
    public ResponseEntity<String> save(@RequestBody
                                       @Valid
                                       UsuarioCreateDTO dto) throws NotFoundException {
        service.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Usuario guardado correctamente");
    }

    /**
     * Función para obtener todos los usuarios.
     * @return Respuesta HTTP con una lista de proyecciones de usuarios.
     */
    @Operation(summary = "Obtener todos los usuarios",
            description = "Devuelve una lista con todos los usuarios." +
                    " **Requiere rol: ADMIN**")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Usuarios encontrados exitosamente"),
            @ApiResponse(responseCode = "400", description = "Error en los datos proporcionados"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    /**
     * Función para obtener un usuario por su ID.
     * @param id del usuario a buscar.
     * @return Respuesta HTTP con la proyección del usuario encontrado.
     * @throws NotFoundException Si no se encuentra el usuario con el ID proporcionado.
     */
    @Operation(summary = "Obtener un usuario por ID",
            description = "Recibe un ID y devuelve el usuario correspondiente." +
                    "** Requiere rol: ADMIN**")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Devuelve el usuario correspondiente"),
            @ApiResponse(responseCode = "404", description = "No se encontró el usuario con el ID proporcionado"),
            @ApiResponse(responseCode = "400", description = "Error en los datos proporcionados"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> findById(@PathVariable
                                                        Long id) throws NotFoundException {
        return ResponseEntity.ok(service.findById(id));
    }

    @Operation(summary = "Obtener usuario autenticado (me)",
            description = "Devuelve el perfil del usuario autenticado. Requiere JWT válido.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me")
    public ResponseEntity<UsuarioResponseDTO> getMe() throws NotFoundException, BadRequestException {
        return ResponseEntity.ok(service.getMe());
    }

    /**
     * Función para actualizar usuario autenticado
     * @return Respuesta HTTP con un mensaje de éxito.
     * @throws NotFoundException Si no se encuentra el usuario con el ID proporcionado.
     * @throws BadRequestException Si los datos proporcionados son inválidos.
     */
    @Operation(summary = "Actualizar usuario autenticado",
            description = "Recibe un ID y un usuario y actualiza el usuario correspondiente." +
            "** El propio usuario autenticado puede actualizar su información**")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Usuario actualizado exitosamente"),
            @ApiResponse(responseCode = "401",description = "Usuario no autenticado"),
            @ApiResponse(responseCode = "404", description = "No se encontró el usuario con el ID proporcionado"),
            @ApiResponse(responseCode = "400", description = "Error en los datos proporcionados"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/me")
    public ResponseEntity<UsuarioResponseDTO> updateMe(@RequestBody @Valid UsuarioUpdateDTO dto)
            throws NotFoundException, BadRequestException {
        return ResponseEntity.ok(service.updateMe(dto));
    }

    /**
     * Función para actualizar un usuario por su ID.
     * @param id del usuario a actualizar.
     * @param dto Objeto DTO que contiene los nuevos datos del usuario.
     * @return Respuesta HTTP con un mensaje de éxito.
     * @throws NotFoundException Si no se encuentra el usuario con el ID proporcionado.
     * @throws BadRequestException Si los datos proporcionados son inválidos.
     */
    @Operation(summary = "Actualizar un usuario",
            description = "Recibe un ID y un usuario y actualiza el usuario correspondiente." +
                    "** Necesita Rol Admin**")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Usuario actualizada exitosamente"),
            @ApiResponse(responseCode = "404", description = "No se encontró el usuario con el ID proporcionado"),
            @ApiResponse(responseCode = "400", description = "Error en los datos proporcionados"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN")
    @PatchMapping("/{id}")
    public ResponseEntity<String> updatePerfil(@PathVariable
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
    @Operation(summary = "Eliminar un usuario",
            description = "Recibe un ID y elimina el usuario correspondiente." +
                    "** El propio usuario autenticado puede borrar su información**")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204",description = "Usuario eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "No se encontró el usuario con el ID proporcionado"),
            @ApiResponse(responseCode = "400", description = "Error en los datos proporcionados"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable
                                         Long id) throws NotFoundException {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Función para eliminar un usuario autenticado
     * @return Respuesta HTTP con un mensaje de éxito.
     * @throws NotFoundException Si no se encuentra el usuario autenticado
     * @throws BadRequestException si ocurre un error con el jwt.
     */
    @Operation(summary = "Eliminar un usuario autenticado",
            description = "Elimina el usuario autenticado." +
                    "** El propio usuario autenticado puede borrar su información**")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204",description = "Usuario eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "No se encontró el usuario con el ID proporcionado"),
            @ApiResponse(responseCode = "400", description = "Error en los datos proporcionados"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/me")
    public ResponseEntity<String> deleteMe() throws NotFoundException, BadRequestException {
        service.deleteMe();
        return ResponseEntity.noContent().build();
    }

    /**
     * Función para actualizar la contraseña de un usuario por su ID.
     * @param dto con la contraseña actual y la nueva.
     * @return Respuesta HTTP con un mensaje de éxito.
     * @throws NotFoundException Si no se encuentra el usuario con el ID proporcionado.
     */
    @Operation(summary = "Actualizar la contraseña del usuario autenticado",
            description = "Recibe la vieja contraseña y la nueva contraseña y actualiza el usuario autenticado." +
                    "** El propio usuario autenticado puede actualizar su contraseña**")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Usuario actualizada exitosamente"),
            @ApiResponse(responseCode = "404", description = "No se encontró el usuario con el ID proporcionado"),
            @ApiResponse(responseCode = "400", description = "Error en los datos proporcionados"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/password")
    public ResponseEntity<String> changePassword(
            @RequestBody @Valid
            ChangePasswordDTO dto) throws NotFoundException, BadRequestException {
        service.changePassword(dto);
        return ResponseEntity.ok().body("Contraseña actualizada exitosamente");
    }

    /**
     * Función para obtener todos los usuarios por IDRol.
     * @param id del rol a buscar.
     * @return Respuesta HTTP con una lista de proyecciones de usuarios.
     * @throws NotFoundException Si no se encuentra el rol con el ID proporcionado.
     */
    @Operation(summary = "Obtener todos los usuarios por rol",
            description = "Devuelve una lista con todos los usuarios que tienen el rol con el ID proporcionado." +
                    "** Requiere rol: ADMIN**")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Promoción creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Error en los datos proporcionados"),
            @ApiResponse(responseCode = "404", description = "No se encontró el rol con el ID proporcionado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/rol/{id}")
    public ResponseEntity<List<UsuarioResponseDTO>> findByRolId(@PathVariable Long id)
            throws NotFoundException {
        return ResponseEntity.ok(service.findByRolId(id));
    }
}
