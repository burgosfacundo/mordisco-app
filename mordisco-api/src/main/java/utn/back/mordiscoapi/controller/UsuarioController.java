package utn.back.mordiscoapi.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import utn.back.mordiscoapi.exception.BadRequestException;
import utn.back.mordiscoapi.exception.NotFoundException;
import utn.back.mordiscoapi.model.dto.usuario.UsuarioCreateDTO;
import utn.back.mordiscoapi.model.dto.usuario.UsuarioResponseDTO;
import utn.back.mordiscoapi.model.dto.usuario.UsuarioUpdateDTO;
import utn.back.mordiscoapi.service.interf.IUsuarioService;

import java.util.List;

@Tag(name = "Usuarios", description = "Operaciones relacionadas a usuarios")
@RestController
@RequestMapping("/api/usuario")
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
            @ApiResponse(responseCode = "200",description = "Promoción creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Error en los datos proporcionados"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping("/save")
    public ResponseEntity<String> save(@RequestBody
                                       @Valid
                                       UsuarioCreateDTO dto) throws NotFoundException {
        service.save(dto);
        return ResponseEntity.ok("Usuario guardado correctamente");
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
                    "** Requiere rol: ADMIN o el propio usuario autenticado puede acceder a su información**")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Devuelve el usuario correspondiente"),
            @ApiResponse(responseCode = "404", description = "No se encontró el usuario con el ID proporcionado"),
            @ApiResponse(responseCode = "400", description = "Error en los datos proporcionados"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN') or @usuarioSecurity.puedeAccederAUsuario(#id)")
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> findById(@PathVariable
                                                        Long id) throws NotFoundException {
        return ResponseEntity.ok(service.findById(id));
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
                    "** El propio usuario autenticado puede actualizar su información**")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Usuario actualizada exitosamente"),
            @ApiResponse(responseCode = "404", description = "No se encontró el usuario con el ID proporcionado"),
            @ApiResponse(responseCode = "400", description = "Error en los datos proporcionados"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("@usuarioSecurity.puedeAccederAUsuario(#id)")
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
    @Operation(summary = "Eliminar un usuario",
            description = "Recibe un ID y elimina el usuario correspondiente." +
                    "** El propio usuario autenticado puede borrar su información**")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Usuario eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "No se encontró el usuario con el ID proporcionado"),
            @ApiResponse(responseCode = "400", description = "Error en los datos proporcionados"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("@usuarioSecurity.puedeAccederAUsuario(#id)")
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
     */
    @Operation(summary = "Actualizar la contraseña de un usuario",
            description = "Recibe un ID, la vieja contraseña y la nueva contraseña y actualiza el usuario correspondiente." +
                    "** El propio usuario autenticado puede actualizar su contraseña**")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Usuario actualizada exitosamente"),
            @ApiResponse(responseCode = "404", description = "No se encontró el usuario con el ID proporcionado"),
            @ApiResponse(responseCode = "400", description = "Error en los datos proporcionados"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("@usuarioSecurity.puedeAccederAUsuario(#id)")
    @PutMapping("/password/{id}")
    public ResponseEntity<String> changePassword(
                                         @Valid
                                         @PathVariable
                                         Long id,
                                         @RequestParam
                                         String oldPassword,
                                         @RequestParam
                                         String newPassword) throws NotFoundException {
        service.changePassword(oldPassword,newPassword,id);
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
