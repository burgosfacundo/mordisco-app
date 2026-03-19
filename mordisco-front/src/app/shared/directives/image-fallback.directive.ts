import { Directive, ElementRef, HostListener, Input } from '@angular/core';

@Directive({
  selector: 'img[appImageFallback]',
  standalone: true
})
export class ImageFallbackDirective {
  // SVG inline como fallback - más confiable que URLs externas
  private readonly defaultFallback = 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNDAwIiBoZWlnaHQ9IjMwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KICA8cmVjdCB3aWR0aD0iNDAwIiBoZWlnaHQ9IjMwMCIgZmlsbD0iI2Y5ZmFmYiIvPgogIDxnPgogICAgPCEtLSBJY29ubyBkZSBpbWFnZW4gLS0+CiAgICA8cGF0aCBkPSJNMTUwIDEyMGMxNi41NyAwIDMwLTEzLjQzIDMwLTMwczEzLjQzLTMwIDMwLTMwIDMwIDEzLjQzIDMwIDMwLTEzLjQzIDMwLTMwIDMwYy0xNi41NyAwLTMwIDEzLjQzLTMwIDMweiIgZmlsbD0iI2QxZDVkYiIvPgogICAgPHBhdGggZD0iTTk1IDE4MGwzNS00MCAxNSAyMCAzNS00MCA1NSA2MHYzMEg5NXoiIGZpbGw9IiNkMWQ1ZGIiLz4KICA8L2c+ICA8dGV4dCB4PSI1MCUiIHk9IjY1JSIgZm9udC1mYW1pbHk9IkFyaWFsLCBzYW5zLXNlcmlmIiBmb250LXNpemU9IjE2IiBmaWxsPSIjOWNhM2FmIiB0ZXh0LWFuY2hvcj0ibWlkZGxlIj5JbWFnZW4gbm8gZGlzcG9uaWJsZTwvdGV4dD4KPC9zdmc+';

  @Input() appImageFallback: string = this.defaultFallback;

  private hasErrored = false;

  constructor(private el: ElementRef) {}

  @HostListener('error')
  onError() {
    if (!this.hasErrored) {
      this.hasErrored = true;
      const element = this.el.nativeElement as HTMLImageElement;
      element.src = this.appImageFallback || this.defaultFallback;
      element.classList.add('fallback-image');
    }
  }

  @HostListener('load')
  onLoad() {
    // Resetear el flag si la imagen carga exitosamente
    this.hasErrored = false;
  }
}
